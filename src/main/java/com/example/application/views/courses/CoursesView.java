package com.example.application.views.courses;

import com.example.application.data.Courses;
import com.example.application.data.Teachers;
import com.example.application.services.CoursesService;
import com.example.application.services.TeachersService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Courses")
@Route(value = "courses/:coursesID?/:action?(edit)", layout = MainLayout.class)
@Menu(order = 2, icon = LineAwesomeIconUrl.BOOK_SOLID)
public class CoursesView extends Div implements BeforeEnterObserver {

    private final String COURSES_ID = "coursesID";
    private final String COURSES_EDIT_ROUTE_TEMPLATE = "courses/%s/edit";

    private final Grid<Courses> grid = new Grid<>(Courses.class, false);

    private TextField name;
    private ComboBox<Teachers> teacher;
    private TextField about;
    private TextField difficulty;
    private TextField credits;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private final BeanValidationBinder<Courses> binder;

    private Courses courses;

    private final CoursesService coursesService;
    private final TeachersService teachersService;

    public CoursesView(CoursesService coursesService, TeachersService teachersService) {
        this.coursesService = coursesService;
        this.teachersService = teachersService;
        addClassNames("courses-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        delete.setEnabled(false);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn(course -> course.getTeacher() != null ? course.getTeacher().getFullName() : "").setHeader("Opettaja");

        grid.addColumn("about").setAutoWidth(true);
        grid.addColumn("difficulty").setAutoWidth(true);
        grid.addColumn("credits").setAutoWidth(true);
        grid.addColumn(course -> course.getStudents().size())
                .setHeader("Students")
                .setAutoWidth(true);
        grid.setItems(query -> coursesService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(COURSES_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(CoursesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Courses.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(difficulty)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("difficulty");
        binder.forField(credits)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("credits");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.courses == null) {
                    this.courses = new Courses();
                }
                binder.writeBean(this.courses);
                coursesService.save(this.courses);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(CoursesView.class);
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
        Optional<Long> coursesId = event.getRouteParameters().get(COURSES_ID).map(Long::parseLong);
        if (coursesId.isPresent()) {
            Optional<Courses> coursesFromBackend = coursesService.get(coursesId.get());
            if (coursesFromBackend.isPresent()) {
                populateForm(coursesFromBackend.get());
            } else {
                Notification.show(String.format("The requested courses was not found, ID = %s", coursesId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(CoursesView.class);
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
        name = new TextField("Name");
        teacher = new ComboBox<>("Teacher");
        teacher.setItems(teachersService.list(Pageable.unpaged()).getContent());
        teacher.setItemLabelGenerator(Teachers::getFullName);
        about = new TextField("About");
        difficulty = new TextField("Difficulty");
        credits = new TextField("Credits");
        formLayout.add(name, teacher, about, difficulty, credits);

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
        if (this.courses == null || this.courses.getId() == null) {
            return;
        }

        Dialog dialog = new Dialog();
        dialog.add("Delete this course?");

        Button confirm = new Button("Delete", event -> {
            coursesService.delete(this.courses.getId());
            dialog.close();
            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(CoursesView.class);
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

    private void populateForm(Courses value) {
        this.courses = value;
        delete.setEnabled(value != null);
        binder.readBean(this.courses);

    }
}
