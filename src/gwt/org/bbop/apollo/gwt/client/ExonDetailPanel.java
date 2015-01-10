package org.bbop.apollo.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.json.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.TextBox;

/**
 * Created by ndunn on 1/9/15.
 */
public class ExonDetailPanel extends Composite {

    interface ExonDetailPanelUiBinder extends UiBinder<Widget, ExonDetailPanel> {
    }

    Dictionary dictionary = Dictionary.getDictionary("Options");
    String rootUrl = dictionary.get("rootUrl");
    private JSONObject internalData;

    private static ExonDetailPanelUiBinder ourUiBinder = GWT.create(ExonDetailPanelUiBinder.class);
    @UiField
    TextBox maxField;
    @UiField
    TextBox minField;
    @UiField
    Button positiveStrandValue;
    @UiField
    Button negativeStrandValue;

    private void enableFields(boolean enabled) {
        minField.setEnabled(enabled);
        maxField.setEnabled(enabled);
        positiveStrandValue.setEnabled(enabled);
        negativeStrandValue.setEnabled(enabled);
    }


//    @UiField
//    org.gwtbootstrap3.client.ui.TextBox nameField;
//    @UiField
//    org.gwtbootstrap3.client.ui.TextBox symbolField;
//    @UiField
//    org.gwtbootstrap3.client.ui.TextBox descriptionField;
//    @UiField
//    InputGroupAddon locationField;
//    @UiField
//    RadioButton strandButton;


    public ExonDetailPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void updateData(JSONObject internalData) {
        this.internalData = internalData;
        GWT.log("updating exon detail panel");
        GWT.log(internalData.toString());
//        nameField.setText(internalData.get("name").isString().stringValue());

        JSONObject locationObject = this.internalData.get("location").isObject();
        minField.setText(locationObject.get("fmin").isNumber().toString());
        maxField.setText(locationObject.get("fmax").isNumber().toString());

        if (locationObject.get("strand").isNumber().doubleValue() > 0) {
            positiveStrandValue.setActive(true);
            negativeStrandValue.setActive(false);
        } else {
            positiveStrandValue.setActive(false);
            negativeStrandValue.setActive(true);
        }


        setVisible(true);
    }

    @UiHandler("minField")
    void handleMinChange(ChangeEvent e) {
        String updatedName = minField.getText();
        JSONObject locationObject = internalData.get("location").isObject();
        locationObject.put("fmin", new JSONNumber(Integer.parseInt(updatedName)));
        updateExon(internalData);
    }

    @UiHandler("maxField")
    void handleMaxChange(ChangeEvent e) {
        String updatedName = maxField.getText();
        JSONObject locationObject = internalData.get("location").isObject();
        locationObject.put("fmax", new JSONNumber(Integer.parseInt(updatedName)));
        updateExon(internalData);
    }

    @UiHandler("positiveStrandValue")
    void handlePositiveStrand(ClickEvent e) {
        if (negativeStrandValue.isActive()) {
            JSONObject locationObject = this.internalData.get("location").isObject();
            locationObject.put("strand", new JSONNumber(1));
            updateExon(internalData);
            positiveStrandValue.setActive(true);
            negativeStrandValue.setActive(false);
        }
    }

    @UiHandler("negativeStrandValue")
    void handleNegativeStrand(ClickEvent e) {
        if (positiveStrandValue.isActive()) {
            JSONObject locationObject = this.internalData.get("location").isObject();
            locationObject.put("strand", new JSONNumber(-1));
            updateExon(internalData);
            updateExon(internalData);
            positiveStrandValue.setActive(false);
            negativeStrandValue.setActive(true);
        }
    }


    private void updateExon(JSONObject internalData) {
        String url = rootUrl + "/annotator/updateExon";
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
        builder.setHeader("Content-type", "application/x-www-form-urlencoded");
        StringBuilder sb = new StringBuilder();
        sb.append("data=" + internalData.toString());
//        sb.append("&key2=val2");
//        sb.append("&key3=val3");
        builder.setRequestData(sb.toString());
        enableFields(false);
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                JSONValue returnValue = JSONParser.parseStrict(response.getText());
//                Window.alert("successful update: "+returnValue);
                enableFields(true);
            }

            @Override
            public void onError(Request request, Throwable exception) {
                Window.alert("Error updating gene: " + exception);
                enableFields(true);
            }
        };
        try {
            builder.setCallback(requestCallback);
            builder.send();
            enableFields(true);
        } catch (RequestException e) {
            // Couldn't connect to server
            Window.alert(e.getMessage());
            enableFields(true);
        }

    }
}
