package com.example.application.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("Opiskelijahallinta");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        // Show menu entries only to authenticated users. For anonymous users show only the home link.
        if (isUserLoggedIn()) {
            List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
            menuEntries.forEach(entry -> {
                if (entry.icon() != null) {
                    nav.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
                } else {
                    nav.addItem(new SideNavItem(entry.title(), entry.path()));
                }
            });
        } else {
            // add only the root/home link
            nav.addItem(new SideNavItem("Etusivu", ""));
        }

        return nav;
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

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.addClassName("main-footer");

        // Copyright and year
        Span copyright = new Span("© 2026 Opiskelijahallintajärjestelmä");

        // Author info
        Span author = new Span("Teemu Lötjönen");
        author.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        // Version or info
        Span version = new Span("Versio 1.0");
        version.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        footer.add(
            copyright,
            new Span(" | "),
            author,
            new Span(" | "),
            version
        );

        return footer;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}
