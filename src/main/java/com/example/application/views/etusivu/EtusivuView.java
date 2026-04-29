package com.example.application.views.etusivu;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("Etusivu")
@Route(value = "", layout = MainLayout.class)
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
@AnonymousAllowed
public class EtusivuView extends VerticalLayout {

    public EtusivuView() {
        setSpacing(false);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        H2 header = new H2("Tervetuloa opiskelijahallintajärjestelmään!");
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        add(header);
        add(new Paragraph("Täällä voit hallita opiskelijoita, kursseja ja opettajia"));

        Button loginBtn = new Button("Kirjaudu sisään", e -> UI.getCurrent().navigate("login"));
        loginBtn.getElement().getThemeList().add("primary");
        // show login button only for anonymous users
        loginBtn.setVisible(!isUserLoggedIn());

        Button logoutBtn = new Button("Kirjaudu ulos", e ->
                UI.getCurrent().getPage().executeJs(
                        "const f=document.createElement('form');"
                                + "f.method='post';"
                                + "f.action='logout';"
                                + "document.body.appendChild(f);"
                                + "f.submit();"
                )
        );
        logoutBtn.getElement().getThemeList().add("error");
        // show logout button only for authenticated users
        logoutBtn.setVisible(isUserLoggedIn());

        add(loginBtn, logoutBtn);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        if (!authentication.isAuthenticated()) {
            return false;
        }
        return !(authentication instanceof AnonymousAuthenticationToken);
    }

}
