package SE2203b.Assignment1.views;

import SE2203b.Assignment1.Domain.Assessment;
import SE2203b.Assignment1.service.CoursePlannerService;
import SE2203b.Assignment1.service.GradeCalculator;
import com.vaadin.flow.component.ModalityMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import java.util.concurrent.atomic.AtomicBoolean;

@Route("")
public class HomeView extends VerticalLayout {
    // Navigation buttons for switching between UI sections
    private Button planner, summary, help;

    // Page title and current section indicator
    private H1 title;
    private NativeLabel currentSection;

    // Content area where the current screen gets placed
    private HorizontalLayout content;

    //Layout for the help popup
    private Dialog helpDialog;

    //service layer for assessments
    private CoursePlannerService service = new CoursePlannerService();

    /****************************************
        planner UI components (form + grid)
     ***************************************/
    private TextField name;
    private ComboBox<String> type;
    private NumberField weight;
    private NumberField mark;
    private Checkbox marked;

    private Button save, clear, delete;

    private Grid<Assessment> display;
    private HorizontalLayout plannerLayout;

    //Binder connects UI fields to the Assessments & handles validation, reads, and writes
    private Binder<Assessment> binder;
    private Binder.Binding<Assessment, String> nameBinding;

    //makes it possible for the name field to be explicitly validated on save
    private final AtomicBoolean attemptedSave = new AtomicBoolean(false);

    /*
     * Selected assessment from the grid:
     * null means we are creating a new assessment
     * non-null means we are editing an existing assessment
     */
    private Assessment selected;

    /*********************
        Summary Fields
     ********************/

    VerticalLayout summaryLayout;
    NativeLabel total, weightedGrade, remainingWeight, requiredAverageLabel;
    NumberField target;

    /**
     * HomeView: constructs the UI by calling the init method, and handles the top level event listeners
     */
    public HomeView() {
        init();

        planner.addClickListener(e -> {
            reset("Planner");
            content.add(plannerLayout);
        });

        summary.addClickListener(e -> {
            reset("Summary");
            content.add(summaryLayout);
        });

        help.addClickListener(e -> {
            helpDialog.open();
        });
    }

    /**
     * reset: updates view title + current section label and clears the content area
     *
     * @param menuName the screen name to display in UI labels
     */
    private void reset(String menuName){
        title.setText(menuName);
        currentSection.setText("Current section: " + menuName);
        content.removeAll();
    }

    /**
     * init: initializes the top level navigation UI and calls methods to build each section
     */
    private void init(){
        // Navigation buttons for switching between sections
        planner = new Button("Planner");
        summary = new Button("Summary");
        help = new Button("Help");

        // Top menu bar containing the navigation buttons
        HorizontalLayout menu = new HorizontalLayout(planner, summary, help);

        // Page title and current section indicator
        currentSection = new NativeLabel("Current section: Planner");
        title = new H1("Planner");

        //main content container that will hold the active screen (Planner, Summary, or help)
        content = new HorizontalLayout();
        content.setWidthFull();

        //add base UI elements to the page
        add(menu, currentSection, title, content);

        //build each UI page
        initPlanner();
        initSummary();
        initHelp();

        content.add(plannerLayout);
    }

    /********************
        Planner methods
     *******************/

    /**
     * initPlanner: calls all Planner setup methods
     */
    private void initPlanner(){
        buildPlannerFields();
        buildPlannerLayout();
        configurePlannerBinder();
        configurePlannerEvents();
    }

    /**
     * buildPlannerFields: instantiates and configures planner components
     */
    private void buildPlannerFields(){
        //form fields for creating/editiing an assessment
        name = new TextField("Assessment name*");
        type = new ComboBox<>("Type");
        weight = new NumberField("Weight (%)");
        mark = new NumberField("Mark (%)");
        marked = new Checkbox("Marked?");

        //buttons to make the form usable
        save = new Button("Save");
        clear = new Button("Clear");
        delete = new Button("Delete");

        //UI sizing so fields look better
        name.setWidth("250px");
        type.setWidth("250px");
        weight.setWidth("250px");
        mark.setWidth("250px");

        //mark is disabled by default, and only enabled when marked is checked
        mark.setEnabled(false);

        //dropdown options for assessment category
        type.setItems("Lab", "Quiz", "Assignment", "Midterm", "Final", "Project", "Other");
        type.setValue("Other");

        delete.setEnabled(false);

        name.setValueChangeMode(ValueChangeMode.EAGER);
    }

    /**
     * buildPlannerLayout: Builds the Planner layout:
     * left: form fields and action buttons
     * right: grid showing all stored assessments
     */
    private void buildPlannerLayout() {
        //place the buttons side by side
        HorizontalLayout buttons = new HorizontalLayout(save, clear, delete);

        //arrange form fields vertically
        VerticalLayout form = new VerticalLayout(name, type, weight, marked, mark, buttons);
        form.setWidth("360px");

        //grid to display assessment objects
        display = new Grid<>();
        display.addColumn(Assessment::getName).setHeader("Name");
        display.addColumn(Assessment::getType).setHeader("Type");
        display.addColumn(Assessment::getWeight).setHeader("Weight (%)");
        display.addColumn(a -> a.getMarked() ? "Yes" : "No").setHeader("Marked?");
        display.addColumn(Assessment::getMark).setHeader("Mark (%)");

        display.setWidthFull();

        //puts the form on the left and the grid of assessments on the right
        plannerLayout = new HorizontalLayout(form, display);
        plannerLayout.setWidthFull();
    }

    /**
     * configurePlannerBinder: makes Vaadin Binder bindings and validators.
     * Enforces rules such as:
     * - name required and has a minimum length
     * - name must be unique
     * - weight is in the range (0, 100]
     * - mark only required when marked is checked
     */
    private void configurePlannerBinder(){
        binder = new Binder<>(Assessment.class);

        nameBinding = binder.forField(name)
                .withValidator(n -> {
                    //Only enforce required after a save attempt
                    if(!attemptedSave.get() || !n.isEmpty()) {
                        return true;
                    }else {
                        attemptedSave.set(false);
                        return false;
                    }
                }, "Name is required")
                .withValidator(n -> {
                    return n.trim().length() >= 3;
                }, "Name must be at least 3 characters")
                .withValidator(n -> {
                    return service.isNameUnique(n, selected);
                }, "Duplicate assessment name")
                .bind(Assessment::getName, Assessment::setName);

        binder.forField(type).bind(Assessment::getType, Assessment::setType);

        binder.forField(weight)
                .withValidator(w -> w != null && w > 0 && w <= 100, "Weight must be in (0,100]")
                .bind(Assessment::getWeight, Assessment::setWeight);
        binder.forField(marked).bind(Assessment::getMarked, Assessment::setMarked);

        binder.forField(mark).withValidator(e -> {
            //when not marked, mark is ignored
            boolean markedBool = marked.getValue();

            if(!markedBool) return true;

            //when marked, mark must be within [0,100]
            return e != null && e >= 0 && e <= 100;
            }, "Mark is required and must be in range when Marked is checked")
                .bind(Assessment::getMark, Assessment::setMark);
    }

    /**
     * configurePlannerEvents: Attaches event listeners to planner elements
     * - live validation of assessment name
     * - enable/disable mark field based on marked
     * - selecting a grid row loads that assessment into the form for editing
     * - save creates or updates, delete removes, clear resets form
     */
    private void configurePlannerEvents() {
        //re-validate name
        name.addValueChangeListener(e -> nameBinding.validate());

        //mark field is only considered when marked is checked
        marked.addValueChangeListener(e -> {
            boolean markedBool = marked.getValue();
            mark.setEnabled(markedBool);

            //clear mark if marked is unchecked
            if(!markedBool)
                mark.clear();
        });

        //selecting an assessment on the grid enables edit mode
        display.asSingleSelect().addValueChangeListener(e ->{
            selected = e.getValue();
            delete.setEnabled(selected != null);
            if(selected != null)
                binder.readBean(selected);
            else//for when the form is cleared while an assessment is selected
                clearForm();
        });

        //either update an existing Assessment or create a new one and save it
        save.addClickListener(e -> {

            Assessment target = (selected != null) ? selected : new Assessment();

            attemptedSave.set(true);

            if(binder.writeBeanIfValid(target)) {
                //only add to storage if this is a new Assessment
                if(selected == null)
                    service.save(target);

                Notification.show("Saved.");

                //Refresh grid with updated list
                display.setItems(service.getAll());
                display.getDataProvider().refreshAll();

                clearForm();
                updateSummary();
            } else {
                Notification.show("Fix validation errors and try again.");
            }
        });

        clear.addClickListener(e -> clearForm());

        delete.addClickListener(e -> {
            if(selected == null) return;
            service.delete(selected);
            display.setItems(service.getAll());
            display.getDataProvider().refreshAll();
            Notification.show("Deleted");
            clearForm();
            updateSummary();
        });
    }

    /**
     * clearForm: Resets the form to default mode, clears all selections
     */
    private void clearForm() {
        binder.readBean(new Assessment());

        type.setValue("Other");

        selected = null;
        mark.setEnabled(false);
        delete.setEnabled(false);
        display.asSingleSelect().clear();
    }

    /**
     * initSummary: Creates the Summary screen UI, this is where grade totals and required averages are shown
     */
    private void initSummary(){
        total = new NativeLabel("Total marked weight: 0.0%");
        weightedGrade = new NativeLabel("Weighted grade so far (marked only): 0.0%");
        remainingWeight = new NativeLabel("Remaining weight to reach 100%%: 0.0%");
        requiredAverageLabel = new NativeLabel("Required average on remaining: 0.0%");

        target = new NumberField("Target overall (%)");

        //arranges summary elements vertically
        summaryLayout = new VerticalLayout(total, weightedGrade, remainingWeight, target, requiredAverageLabel);
        summaryLayout.setWidthFull();

        target.setValueChangeMode(ValueChangeMode.EAGER);

        target.addValueChangeListener(e -> {
            updateRequiredAverageLabel();
        });
    }

    /**
     * updateSummary: Recomputes all summary labels from current list of assessments.
     */
    private void updateSummary() {
        total.setText(String.format("Total marked weight: %.1f%%",
                GradeCalculator.totalMarkedWeight(service.getAll())));
        weightedGrade.setText(String.format("Weighted grade so far (marked only): %.1f%%",
                GradeCalculator.weightedGrade(service.getAll())));
        remainingWeight.setText(String.format("Remaining weight to reach 100%%: %.1f%%",
                GradeCalculator.remainingWeight(service.getAll())));

        updateRequiredAverageLabel();
    }

    /**
     * updateRequiredAverageLabel: Updates only the required average label
     */
    private void updateRequiredAverageLabel(){
        String requiredAverage = target.getValue() != null && GradeCalculator.totalMarkedWeight(service.getAll()) != 100 ?
                String.format("%.1f%%", GradeCalculator.requiredAverage(target.getValue(), service.getAll())) : "";
        requiredAverageLabel.setText("Required average on remaining: " + requiredAverage);
    }

    /**
     * initHelp: Creates the Help screen UI, explains how to use the grade calculator
     */
    private void initHelp() {
        helpDialog = new Dialog();
        // Adding a simple help dialog that has a label and a close button
        NativeLabel helpLabel = new NativeLabel("Grade Planner Help");
        NativeLabel dialogPlanner = new NativeLabel(
                "Planner section: Enter each assessment’s details and click Save. Select a row to edit or delete it."
        );
        NativeLabel dialogSummary = new NativeLabel(
                "Summary section: Displays progress based on marked assessments and calculates the required average for your target."
        );
        NativeLabel dialogRules = new NativeLabel(
                "Validation: Assessment name must be unique and at least 3 characters. Weight must be within (0,100]. Mark must be 0–100 when enabled."
        );

        helpLabel.getStyle().set("font-weight", "bold");

        Button dialogClose = new Button("Close", e -> {
            // close the dialog
            helpDialog.close();
        });
        HorizontalLayout buttonBar = new HorizontalLayout(JustifyContentMode.END, dialogClose);
        buttonBar.setWidthFull();

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(helpLabel, dialogPlanner, dialogSummary, dialogRules, buttonBar);

        // Prevent users from closing the dialog by clicking outside or pressing Escape
        helpDialog.setCloseOnEsc(false);
        helpDialog.setCloseOnOutsideClick(false);
        // Block all background interaction
        helpDialog.setModality(ModalityMode.STRICT);
        helpDialog.add(dialogLayout);
    }
}
