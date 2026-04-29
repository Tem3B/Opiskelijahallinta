package com.example.application.views.offline;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Offline")
@Route("offline-stub.html")
@AnonymousAllowed
public class OfflineStubView extends Div {

    public OfflineStubView() {
        setVisible(false);
    }
}

