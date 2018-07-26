/**
 * Cerberus Copyright (C) 2013 - 2017 cerberustesting
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.servlet.crud.transversaltables;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.entity.Label;
import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.exception.CerberusException;
import org.cerberus.crud.service.ILabelService;
import org.cerberus.crud.service.impl.LabelService;
import org.cerberus.util.ParameterParserUtil;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;
import org.cerberus.util.answer.AnswerUtil;
import org.cerberus.util.servlet.ServletUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author bcivel
 */
@WebServlet(name = "ReadLabel", urlPatterns = {"/ReadLabel"})
public class ReadLabel extends HttpServlet {

    private ILabelService labelService;
    private static final Logger LOG = LogManager.getLogger(ReadLabel.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws org.cerberus.exception.CerberusException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, CerberusException {
        String echo = request.getParameter("sEcho");
        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

        response.setContentType("application/json");
        response.setCharacterEncoding("utf8");

        // Calling Servlet Transversal Util.
        ServletUtil.servletStart(request);

        // Default message to unexpected error.
        MessageEvent msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
        msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", ""));

        /**
         * Parsing and securing all required parameters.
         */
        // Nothing to do here as no parameter to check.
        //
        // Global boolean on the servlet that define if the user has permition to edit and delete object.
        boolean userHasPermissions = request.isUserInRole("Label");

        //Get Parameters
        String columnName = ParameterParserUtil.parseStringParam(request.getParameter("columnName"), "");
        Boolean likeColumn = ParameterParserUtil.parseBooleanParam(request.getParameter("likeColumn"), false);

        // Init Answer with potencial error from Parsing parameter.
        AnswerItem answer = new AnswerItem<>(new MessageEvent(MessageEventEnum.DATA_OPERATION_OK));
        AnswerItem answer1 = new AnswerItem<>(new MessageEvent(MessageEventEnum.DATA_OPERATION_OK));

        try {
            JSONObject jsonResponse = new JSONObject();
            if ((request.getParameter("id") == null) && (request.getParameter("system") == null) && Strings.isNullOrEmpty(columnName)) {
                answer = findLabelList(null, appContext, userHasPermissions, request);
                jsonResponse = (JSONObject) answer.getItem();
            } else {
                if (request.getParameter("id") != null) {
                    Integer id = Integer.valueOf(policy.sanitize(request.getParameter("id")));
                    answer = findLabelByKey(id, appContext, userHasPermissions);
                    jsonResponse = (JSONObject) answer.getItem();
                } else if (request.getParameter("system") != null && !Strings.isNullOrEmpty(columnName)) {
                    answer = findDistinctValuesOfColumn(request.getParameter("system"), appContext, request, columnName);
                    jsonResponse = (JSONObject) answer.getItem();
                } else if (request.getParameter("system") != null) {
                    String system = policy.sanitize(request.getParameter("system"));
                    answer = findLabelList(system, appContext, userHasPermissions, request);
                    jsonResponse = (JSONObject) answer.getItem();
                }
            }
            if ((request.getParameter("withHierarchy") != null)) {
                String system = policy.sanitize(request.getParameter("system"));
                answer1 = getLabelHierarchy(system, appContext, userHasPermissions, request);
                JSONObject jsonHierarchy = (JSONObject) answer1.getItem();
                jsonResponse.put("labelHierarchy", jsonHierarchy);

            }

            jsonResponse.put("messageType", answer.getResultMessage().getMessage().getCodeString());
            jsonResponse.put("message", answer.getResultMessage().getDescription());
            jsonResponse.put("sEcho", echo);

            response.getWriter().print(jsonResponse.toString());

        } catch (JSONException e) {
            LOG.warn(e);
            //returns a default error message with the json format that is able to be parsed by the client-side
            response.getWriter().print(AnswerUtil.createGenericErrorAnswer());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (CerberusException ex) {
            LOG.warn(ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (CerberusException ex) {
            LOG.warn(ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private AnswerItem findLabelList(String system, ApplicationContext appContext, boolean userHasPermissions, HttpServletRequest request) throws JSONException {

        AnswerItem item = new AnswerItem<>();
        JSONObject object = new JSONObject();
        labelService = appContext.getBean(LabelService.class);

        int startPosition = Integer.valueOf(ParameterParserUtil.parseStringParam(request.getParameter("iDisplayStart"), "0"));
        int length = Integer.valueOf(ParameterParserUtil.parseStringParam(request.getParameter("iDisplayLength"), "0"));
        /*int sEcho  = Integer.valueOf(request.getParameter("sEcho"));*/

        String searchParameter = ParameterParserUtil.parseStringParam(request.getParameter("sSearch"), "");
        int columnToSortParameter = Integer.parseInt(ParameterParserUtil.parseStringParam(request.getParameter("iSortCol_0"), "1"));
        String sColumns = ParameterParserUtil.parseStringParam(request.getParameter("sColumns"), "System,Label,Color,Display,parentLabelId,Description");
        String columnToSort[] = sColumns.split(",");
        String columnName = columnToSort[columnToSortParameter];
        String sort = ParameterParserUtil.parseStringParam(request.getParameter("sSortDir_0"), "asc");
        List<String> individualLike = new ArrayList<>(Arrays.asList(ParameterParserUtil.parseStringParam(request.getParameter("sLike"), "").split(",")));

        Map<String, List<String>> individualSearch = new HashMap<>();
        for (int a = 0; a < columnToSort.length; a++) {
            if (null != request.getParameter("sSearch_" + a) && !request.getParameter("sSearch_" + a).isEmpty()) {
                List<String> search = new ArrayList<>(Arrays.asList(request.getParameter("sSearch_" + a).split(",")));
                if (individualLike.contains(columnToSort[a])) {
                    individualSearch.put(columnToSort[a] + ":like", search);
                } else {
                    individualSearch.put(columnToSort[a], search);
                }
            }
        }
        AnswerList resp = labelService.readByVariousByCriteria(system, null, startPosition, length, columnName, sort, searchParameter, individualSearch);

        JSONArray jsonArray = new JSONArray();
        if (resp.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {//the service was able to perform the query, then we should get all values
            for (Label label : (List<Label>) resp.getDataList()) {
                JSONObject labelObject = convertLabelToJSONObject(label);
                if (label.getParentLabelID() > 0) {
                    AnswerItem parentLabel = labelService.readByKey(label.getParentLabelID());
                    if (parentLabel.getItem() != null) {
                        labelObject.put("labelParentObject", convertLabelToJSONObject((Label) parentLabel.getItem()));
                    }
                }
                jsonArray.put(labelObject);
            }
        }
        object.put("hasPermissions", userHasPermissions);
        object.put("contentTable", jsonArray);
        object.put("iTotalRecords", resp.getTotalRows());
        object.put("iTotalDisplayRecords", resp.getTotalRows());
        item.setItem(object);
        item.setResultMessage(resp.getResultMessage());
        return item;
    }

    private AnswerItem getLabelHierarchy(String system, ApplicationContext appContext, boolean userHasPermissions, HttpServletRequest request) throws JSONException {

        AnswerItem item = new AnswerItem<>();
        JSONObject object = new JSONObject();

        JSONArray jsonObject = new JSONArray();
        jsonObject = getTree(system, Label.TYPE_REQUIREMENT, appContext);
        object.put("requirements", jsonObject);

        jsonObject = new JSONArray();
        jsonObject = getTree(system, Label.TYPE_STICKER, appContext);
        object.put("stickers", jsonObject);

        jsonObject = new JSONArray();
        jsonObject = getTree(system, Label.TYPE_BATTERY, appContext);
        object.put("batteries", jsonObject);

        item.setItem(object);

        return item;
    }

    private JSONArray getTree(String system, String type, ApplicationContext appContext) throws JSONException {
        labelService = appContext.getBean(LabelService.class);

        List<Label> finalList = new ArrayList<Label>();

        AnswerList resp = labelService.readByVarious(system, type);

        // Building tree Structure;
        if (resp.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            Map<Integer, Label> tree = new HashMap<>();
            Map<Integer, Label> labelList = new HashMap<>();
            for (Label label : (List<Label>) resp.getDataList()) {
                String text = "<span class='label label-primary' style='background-color:" + label.getColor() + "' data-toggle='tooltip' data-labelid='" + label.getId() + "' title='' data-original-title=''>" + label.getLabel() + "</span>";
                text += "<span style='margin-left: 5px; margin-right: 5px;' class=''>" + label.getDescription() + "</span>";
                text += "<span class='badge badge-pill badge-secondary'>" + label.getReqType() + "</span>";
                text += "<span class='badge badge-pill badge-secondary'>" + label.getReqStatus() + "</span>";
                text += "<span class='badge badge-pill badge-secondary'>" + label.getReqCriticity() + "</span>";

                text += "<button id='editLabel' onclick=\"stopPropagation(event);editEntryClick(\'" + label.getId() + "\', \'" + label.getSystem() + "\');\" class='editLabel btn-tree btn btn-default btn-xs margin-right5' name='editLabel' title='Edit Label' type='button'>";
                text += " <span class='glyphicon glyphicon-pencil'></span></button>";
                text += "<button id='deleteLabel' onclick=\"stopPropagation(event);deleteEntryClick(\'" + label.getId() + "\', \'" + label.getLabel() + "\');\" class='deleteLabel btn-tree btn btn-default btn-xs margin-right5' name='deleteLabel' title='Delete Label' type='button'>";
                text += " <span class='glyphicon glyphicon-trash'></span></button>";

                label.setText(text);
                labelList.put(label.getId(), label);
                if (label.getParentLabelID() > 0) {
                    tree.put(label.getParentLabelID(), label);
                }
            }

            // Loop on maximum hierarchi levels.
            int i = 0;
            while (i < 50 && !labelList.isEmpty()) {
//                LOG.debug(i + ".1 : " + labelList);
                List<Label> listToRemove = new ArrayList<Label>();

                for (Map.Entry<Integer, Label> entry : labelList.entrySet()) {
                    Integer key = entry.getKey();
                    Label value = entry.getValue();
//                    LOG.debug(value.getId() + " " + value.getParentLabelID() + " " + value.getNodes().size());
                    if (tree.get(value.getId()) == null) {
                        if ((i == 0) && (value.getNodes().isEmpty())) {
                            value.setNodes(null);
                        }
//                        LOG.debug("Pas de fils.");
                        if (value.getParentLabelID() <= 0) {
//                            LOG.debug("Adding to final result and remove from list." + i);
                            finalList.add(value);
                            listToRemove.add(value);
                        } else {
//                            LOG.debug("Adding to final result and remove from list." + i);
                            // Mettre sur le fils sur son pere.
                            Label toto = labelList.get(value.getParentLabelID());
                            if (toto != null) {
                                List<Label> titi = toto.getNodes();
                                titi.add(value);
                                toto.setNodes(titi);
                                labelList.put(key, toto);
                            }
                            listToRemove.add(value);
                            tree.remove(value.getParentLabelID());
                        }
                    }
                }
                // Removing all entries that has been clasified to finalList.
                for (Label label : listToRemove) {
                    labelList.remove(label.getId());
                }
                i++;
            }
        }
        Gson gson = new Gson();
        JSONArray jsonArray = new JSONArray(gson.toJson(finalList));

        return jsonArray;
    }

    private AnswerItem findLabelByKey(Integer id, ApplicationContext appContext, boolean userHasPermissions) throws JSONException, CerberusException {
        AnswerItem item = new AnswerItem<>();
        JSONObject object = new JSONObject();

        ILabelService labelService = appContext.getBean(ILabelService.class);

        //finds the project     
        AnswerItem answer = labelService.readByKey(id);

        if (answer.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item and convert it to JSONformat
            Label label = (Label) answer.getItem();
            JSONObject labelObject = convertLabelToJSONObject(label);
            if (label.getParentLabelID() > 0) {
                labelObject.put("labelParentObject", convertLabelToJSONObject((Label) labelService.readByKey(label.getParentLabelID()).getItem()));
            }
            JSONObject response = labelObject;
            object.put("contentTable", response);
        }

        object.put("hasPermissions", userHasPermissions);
        item.setItem(object);
        item.setResultMessage(answer.getResultMessage());

        return item;
    }

    private JSONObject convertLabelToJSONObject(Label label) throws JSONException {

        Gson gson = new Gson();
        JSONObject result = new JSONObject(gson.toJson(label));
        JSONObject display = new JSONObject();
        display.put("label", label.getLabel());
        display.put("color", label.getColor());
        result.put("display", display);
        return result;
    }

    private AnswerItem findDistinctValuesOfColumn(String system, ApplicationContext appContext, HttpServletRequest request, String columnName) throws JSONException {
        AnswerItem answer = new AnswerItem<>();
        JSONObject object = new JSONObject();

        labelService = appContext.getBean(ILabelService.class);

        String searchParameter = ParameterParserUtil.parseStringParam(request.getParameter("sSearch"), "");
        String sColumns = ParameterParserUtil.parseStringParam(request.getParameter("sColumns"), "System,Label,Color,Display,parentLabelId,Description");
        String columnToSort[] = sColumns.split(",");

        List<String> individualLike = new ArrayList<>(Arrays.asList(ParameterParserUtil.parseStringParam(request.getParameter("sLike"), "").split(",")));

        Map<String, List<String>> individualSearch = new HashMap<>();
        for (int a = 0; a < columnToSort.length; a++) {
            if (null != request.getParameter("sSearch_" + a) && !request.getParameter("sSearch_" + a).isEmpty()) {
                List<String> search = new ArrayList<>(Arrays.asList(request.getParameter("sSearch_" + a).split(",")));
                if (individualLike.contains(columnToSort[a])) {
                    individualSearch.put(columnToSort[a] + ":like", search);
                } else {
                    individualSearch.put(columnToSort[a], search);
                }
            }
        }

        AnswerList testCaseList = labelService.readDistinctValuesByCriteria(system, searchParameter, individualSearch, columnName);

        object.put("distinctValues", testCaseList.getDataList());

        answer.setItem(object);
        answer.setResultMessage(testCaseList.getResultMessage());
        return answer;
    }

}
