package com.example.application.views.teachers;

import com.example.application.data.Courses;
import com.example.application.data.Teachers;
import com.example.application.services.CoursesService;
import com.example.application.services.TeachersService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Teachers")
@Route(value = "teachers/:teachersID?/:action?(edit)", layout = MainLayout.class)
@Menu(order = 3, icon = LineAwesomeIconUrl.ARROW_RIGHT_SOLID)
public class TeachersView extends Div implements BeforeEnterObserver {

    private final String TEACHERS_ID = "teachersID";
    private final String TEACHERS_EDIT_ROUTE_TEMPLATE = "teachers/%s/edit";

    private final Grid<Teachers> grid = new Grid<>(Teachers.class, false);

    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField phone;
    private TextField coursesDisplay;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private final BeanValidationBinder<Teachers> binder;

    private Teachers teachers;

    private final TeachersService teachersService;
    private final CoursesService coursesService;

    public TeachersView(TeachersService teachersService, CoursesService coursesService) {
        this.teachersService = teachersService;
        this.coursesService = coursesService;
        addClassNames("teachers-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        delete.setEnabled(false);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn(teacher -> coursesService.findByTeacherId(teacher.getId()).stream()
                .map(Courses::getName).toList().toString())
                .setHeader("Courses")
                .setAutoWidth(true);
        grid.setItems(query -> teachersService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(TEACHERS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(TeachersView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Teachers.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.teachers == null) {
                    this.teachers = new Teachers();
                }
                binder.writeBean(this.teachers);
                teachersService.save(this.teachers);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(TeachersView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });

        delete.addClickListener(e -> openDeleteDialog());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> teachersId = event.getRouteParameters().get(TEACHERS_ID).map(Long::parseLong);
        if (teachersId.isPresent()) {
            Optional<Teachers> teachersFromBackend = teachersService.get(teachersId.get());
            if (teachersFromBackend.isPresent()) {
                populateForm(teachersFromBackend.get());
            } else {
                Notification.show(String.format("The requested teachers was not found, ID = %s", teachersId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(TeachersView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        email = new TextField("Email");
        phone = new TextField("Phone");
        coursesDisplay = new TextField("Courses");
        coursesDisplay.setReadOnly(true);
        formLayout.add(firstName, lastName, email, phone, coursesDisplay);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(delete, save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void openDeleteDialog() {
        if (this.teachers == null) {
            return;
        }

        Dialog dialog = new Dialog();
        dialog.add("Delete this teacher?");

        Button confirm = new Button("Delete", event -> {
            teachersService.delete(this.teachers.getId());
            dialog.close();
            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(TeachersView.class);
        });
        confirm.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        dialog.add(confirm, cancelButton);
        dialog.open();
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Teachers value) {
        this.teachers = value;
        delete.setEnabled(value != null);
        if (value != null) {
            String courseList = coursesService.findByTeacherId(value.getId()).stream()
                    .map(Courses::getName)
                    .toList().toString();
            coursesDisplay.setValue(courseList);
        } else {
            coursesDisplay.setValue("");
        }
        binder.readBean(this.teachers);

    }
}
