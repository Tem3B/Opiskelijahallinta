package com.example.application.views.students;

import com.example.application.data.Courses;
import com.example.application.data.Students;
import com.example.application.services.CoursesService;
import com.example.application.services.StudentsService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Students")
@Route("students/:studentsID?/:action?(edit)")
@Menu(order = 1, icon = LineAwesomeIconUrl.ARROW_RIGHT_SOLID)
public class StudentsView extends Div implements BeforeEnterObserver {

    private final String STUDENTS_ID = "studentsID";
    private final String STUDENTS_EDIT_ROUTE_TEMPLATE = "students/%s/edit";

    private final Grid<Students> grid = new Grid<>(Students.class, false);

    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField phone;
    private TextField street;
    private TextField buildingNumber;
    private TextField city;
    private TextField postalCode;
    private TextField country;
    private MultiSelectComboBox<Courses> courses;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private final BeanValidationBinder<Students> binder;

    private Students students;

    private final StudentsService studentsService;
    private final CoursesService coursesService;

    public StudentsView(StudentsService studentsService, CoursesService coursesService) {
        this.studentsService = studentsService;
        this.coursesService = coursesService;
        addClassNames("students-view");

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
        grid.addColumn(student -> {
            if (student.getAddress() != null) {
                String street = student.getAddress().getStreet();
                return street != null ? street : "";
            }
            return "";
        }).setHeader("Street").setAutoWidth(true);
        grid.addColumn(student -> {
            if (student.getAddress() != null) {
                String buildingNumber = student.getAddress().getBuildingNumber();
                return buildingNumber != null ? buildingNumber : "";
            }
            return "";
        }).setHeader("Building Number").setAutoWidth(true);
        grid.addColumn(student -> {
            if (student.getAddress() != null) {
                String city = student.getAddress().getCity();
                return city != null ? city : "";
            }
            return "";
        }).setHeader("City").setAutoWidth(true);
        grid.addColumn(student -> student.getCourses().stream()
                .map(Courses::getName)
                .collect(Collectors.joining(", ")))
                .setHeader("Courses")
                .setAutoWidth(true);
        grid.setItems(query -> studentsService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(STUDENTS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(StudentsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Students.class);

        // Bind courses field manually with converter (Set<Courses> <-> Set<Courses>)
        binder.forField(courses)
                .bind("courses");

        // Bind address fields
        binder.forField(street)
                .bind(student -> {
                    if (student != null && student.getAddress() != null && student.getAddress().getStreet() != null) {
                        return student.getAddress().getStreet();
                    }
                    return "";
                },
                (student, value) -> {
                    if (student == null) return;
                    if (student.getAddress() == null) {
                        student.setAddress(new com.example.application.data.Address());
                    }
                    student.getAddress().setStreet(value);
                });

        binder.forField(buildingNumber)
                .bind(student -> {
                    if (student != null && student.getAddress() != null && student.getAddress().getBuildingNumber() != null) {
                        return student.getAddress().getBuildingNumber();
                    }
                    return "";
                },
                (student, value) -> {
                    if (student == null) return;
                    if (student.getAddress() == null) {
                        student.setAddress(new com.example.application.data.Address());
                    }
                    student.getAddress().setBuildingNumber(value);
                });

        binder.forField(city)
                .bind(student -> {
                    if (student != null && student.getAddress() != null && student.getAddress().getCity() != null) {
                        return student.getAddress().getCity();
                    }
                    return "";
                },
                (student, value) -> {
                    if (student == null) return;
                    if (student.getAddress() == null) {
                        student.setAddress(new com.example.application.data.Address());
                    }
                    student.getAddress().setCity(value);
                });

        binder.forField(postalCode)
                .bind(student -> {
                    if (student != null && student.getAddress() != null && student.getAddress().getPostalCode() != null) {
                        return student.getAddress().getPostalCode();
                    }
                    return "";
                },
                (student, value) -> {
                    if (student == null) return;
                    if (student.getAddress() == null) {
                        student.setAddress(new com.example.application.data.Address());
                    }
                    student.getAddress().setPostalCode(value);
                });

        binder.forField(country)
                .bind(student -> {
                    if (student != null && student.getAddress() != null && student.getAddress().getCountry() != null) {
                        return student.getAddress().getCountry();
                    }
                    return "";
                },
                (student, value) -> {
                    if (student == null) return;
                    if (student.getAddress() == null) {
                        student.setAddress(new com.example.application.data.Address());
                    }
                    student.getAddress().setCountry(value);
                });

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.students == null) {
                    this.students = new Students();
                }
                binder.writeBean(this.students);
                studentsService.save(this.students);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(StudentsView.class);
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
        Optional<Long> studentsId = event.getRouteParameters().get(STUDENTS_ID).map(Long::parseLong);
        if (studentsId.isPresent()) {
            Optional<Students> studentsFromBackend = studentsService.get(studentsId.get());
            if (studentsFromBackend.isPresent()) {
                populateForm(studentsFromBackend.get());
            } else {
                Notification.show(String.format("The requested students was not found, ID = %s", studentsId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(StudentsView.class);
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
        street = new TextField("Street");
        buildingNumber = new TextField("Building Number");
        city = new TextField("City");
        postalCode = new TextField("Postal Code");
        country = new TextField("Country");
        courses = new MultiSelectComboBox<>("Courses");
        courses.setItems(coursesService.list(Pageable.unpaged()).getContent());
        courses.setItemLabelGenerator(Courses::getName);
        formLayout.add(firstName, lastName, email, phone, street, buildingNumber, city, postalCode, country, courses);

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
        if (this.students == null || this.students.getId() == null) {
            return;
        }

        Dialog dialog = new Dialog();
        dialog.add("Delete this student?");

        Button confirm = new Button("Delete", event -> {
            studentsService.delete(this.students.getId());
            dialog.close();
            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(StudentsView.class);
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

    private void populateForm(Students value) {
        this.students = value;
        delete.setEnabled(value != null);
        if (value != null && !value.getCourses().isEmpty()) {
            courses.setValue(value.getCourses());
        } else {
            courses.setValue(Set.of());
        }
        binder.readBean(this.students);

    }
}
