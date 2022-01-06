import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


// do zrobienia
// wszystko do usytematyzowania i ogarniecia i opisania

public class Main extends Application{
  public static TasksList tasks = new TasksList();
  public static final String saveFile = "tasks.bin";
  public static int type = -1;
  public static Timer timer = new Timer();

  public static void main(String[] args){
    Application.launch(args);
  }

  
  @Override
  public void start(Stage primaryStage) throws Exception {
    loadTasks();

    Calendar calendar = setTime();
    timer.schedule(tasks, calendar.getTime(), TimeUnit.HOURS.toMillis(24));

    HBox hbox = myHBox();
    
    primaryStage.setOnCloseRequest(action -> { 
      saveTasks();
      System.exit(0);
    });

  /* File */
    
    MenuItem menuItemSave = new MenuItem("Zapisz"); 
    menuItemSave.setOnAction(action -> {
      saveTasks();
    });

    MenuItem menuItemAdd = new MenuItem("Dodaj");
    menuItemAdd.setOnAction(action -> {
      try{
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        tasks.addTask(selectedFile.getAbsolutePath());
        displaySuccess("Pomyślnie dodano plik");
      } catch (Exception e){
        displayError(e.getMessage());
      }
    });

    MenuItem menuItemRemove = new MenuItem("Usuń");
    menuItemRemove.setOnAction(action -> {
      removeTask();
    });

    MenuItem menuItemChangePath = new MenuItem("Zmień ścieżkę");
    menuItemChangePath.setOnAction(action -> {
      changeFilePath();
    });

    MenuItem menuItemRunAll = new MenuItem("Wszystkie");
    menuItemRunAll.setOnAction(action -> {
      run();
    });

    MenuItem menuItemRun = new MenuItem("Wybrany");
    menuItemRun.setOnAction(action -> {
      runOneFile();
    });
    Menu subMenuRun = new Menu("Uruchom", null, menuItemRun, menuItemRunAll);

    MenuItem menuItemPrintTasks = new MenuItem("Wypisz");
    menuItemPrintTasks.setOnAction(action -> {
      printTasks();
    });

    MenuItem menuItemBypass = new MenuItem("Wyłącz/Włącz");
    menuItemBypass.setOnAction(action -> {
      bypassTask();
    });
    
    Menu menuFiles = new Menu("Plik", null, subMenuRun, menuItemSave, menuItemAdd, menuItemRemove, menuItemBypass, menuItemChangePath, menuItemPrintTasks);

  /* Macro */
    
    MenuItem menuItemRecord = new MenuItem("Nagraj/Dograj"); 
    menuItemRecord.setOnAction(action -> {
      recordMacro();
    });

    MenuItem menuItemCopyMacro = new MenuItem("Kopiuj");
    menuItemCopyMacro.setOnAction(action -> {
      copyMacro();
    });

    MenuItem menuItemRemoveMacro = new MenuItem("Usuń");
    menuItemRemoveMacro.setOnAction(action -> {
      removeMacro();
    });

    MenuItem menuItemRemoveClicks = new MenuItem("Usuń");
    menuItemRemoveClicks.setOnAction(action -> {
      removeClickFromMacro();
    });

    MenuItem menuItemAddClicks =  new MenuItem("Dodaj kliknięcia"); 
    menuItemAddClicks.setOnAction(action -> {
      addClickToMacro();
    });

    MenuItem menuItemDelay = new MenuItem("Zmień opóźnienie");  
    menuItemDelay.setOnAction(action -> {
      changeDelay();
    });

    MenuItem menuItemPrintClicks = new MenuItem("Wypisz");
    menuItemPrintClicks.setOnAction(action -> {
      printMacroClicksFromTask();
    });

    Menu subMenuClicks = new Menu("Kliknięcia", null, menuItemAddClicks, menuItemRemoveClicks, menuItemDelay, menuItemPrintClicks);
    Menu menuMacro = new Menu("Makro", null, menuItemRecord, menuItemCopyMacro, menuItemRemoveMacro, subMenuClicks);

  /* Settings */

    MenuItem menuItemChangeSchedule = new MenuItem("Zmień godzinę uruchamiania plików");
    menuItemChangeSchedule.setOnAction(action -> {
      changeSchedule();
    });

    MenuItem menuItemInstructions = new MenuItem("Instrukcje pomocnicze"); 
    menuItemInstructions.setOnAction(action -> {
      printInstructions();
    });

    Menu menuSettings = new Menu("Ustawienia", null, menuItemChangeSchedule, menuItemInstructions);

  /* Exiting */
    
    Button exit = new Button("Wyjdź i zapisz");
    exit.setOnAction(action -> {
      saveTasks();
      System.exit(0);
    });

/* --------------------- */

    MenuBar menuBar = new MenuBar(menuFiles, menuMacro, menuSettings);
    hbox.getChildren().addAll(menuBar, exit);

    Scene scene = new Scene(hbox);
    primaryStage.setScene(scene);
    primaryStage.show();
  }



/* STAGES */

// /* MENU FILES */
public static void run(){
  tasks.run();
}

public static void runOneFile(){
  Stage stage = new Stage();
  stage.setTitle("Uruchamianie wybranego pliku");
  VBox vbox = myVBox();

  ListView<String> listView = tasksListView();

  Label label = new Label ("Który plik chcesz uruchomić?:");
  Button button = new Button("Uruchom");

  vbox.getChildren().addAll(label, listView, button);

  button.setOnAction(action -> {
    int selection = listView.getSelectionModel().getSelectedIndex();
    
    tasks.setSingleTaskRun(selection);   
    tasks.run();
  
    stage.close();
  });

  Scene scene = new Scene(vbox, 800, 400);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.show();
}

public static void removeTask(){
  Stage stage = new Stage();
  stage.setTitle("Usuwanie pliku do uruchamiania");
  VBox vbox = myVBox();

  ListView<String> listView = tasksListView();

  Label label = new Label ("Który plik chcesz usunąć?:");
  Button button = new Button("Usuń");

  vbox.getChildren().addAll(label, listView, button);

  button.setOnAction(action -> {    
    try{
      int selection = listView.getSelectionModel().getSelectedIndex();
      tasks.remove(selection );
      displaySuccess("Pomyślnie usunięto plik");
    } catch (Exception e){
      displayError("Nie udało się usunąć pliku");            
    }
    stage.close();
  });

  Scene scene = new Scene(vbox, 800, 400);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.show();
}

public static void changeFilePath(){
  Stage stage = new Stage();
  stage.setTitle("Zmienianie ścieżki do pliku");
  VBox vbox = myVBox();
  

  Label label = new Label ("Któremu plikowi chcesz zmienić ścieżkę?:");
  ListView<String> listView = tasksListView();
  Button button = new Button("Zmień");

  

  button.setOnAction(action -> {
    try{
      FileChooser fileChooser = new FileChooser();
      File selectedFile = fileChooser.showOpenDialog(stage);
      tasks.getTask(listView.getSelectionModel().getSelectedIndex() ).setFilePath(selectedFile.getAbsolutePath());
      displaySuccess("Pomyślnie zmienono ścieżkę do pliku");
    } catch (Exception e){
      displayError(e.getMessage());
    }
    stage.close();
  });

  vbox.getChildren().addAll(label, listView, button);

  Scene scene = new Scene(vbox, 800, 500);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.show();
}

public static void bypassTask(){
  Stage stage = new Stage();
  stage.setTitle("Wyłączanie/Włączanie pliku");
  VBox vbox = myVBox();

  ListView<String> listView = tasksListView();

  Label label = new Label ("Który plik chcesz wyłączyć/włączyć?:");
  Button button = new Button("Włącz/wyłącz");

  vbox.getChildren().addAll(label, listView, button);

  button.setOnAction(action -> {
    try{
      int selection = listView.getSelectionModel().getSelectedIndex();
      tasks.getTask(selection ).setBypassed(); 
    } catch(Exception e){
      displayError(e.getMessage());
    }

    stage.close();
  });

  Scene scene = new Scene(vbox, 800, 400);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.show();
}

public static void printTasks(){
  Stage stage = new Stage();
  stage.setTitle("Pliki");
  VBox vbox = myVBox();
  
  Label label = new Label("Godzina uruchamiania: " + tasks.getHour() + ":00");
  ListView<String> listView = tasksListView();

  vbox.getChildren().addAll(label, listView);

  Scene scene = new Scene(vbox, 800, 400);
  stage.setScene(scene);
  stage.show();
}

/* -------------------------------------------------------- */

// /* MENU MACRO */
public static void recordMacro(){
  Stage stage = new Stage();
  stage.setTitle("Nagrywanie/Dogrywanie(jeśli makro już istnieje)");
  VBox vbox = myVBox();

  ListView<String> listView = tasksListView();

  Label label = new Label ("Dla którego pliku chcesz nagrać makro?:");
  Button button = new Button("Rozpocznij nagrywanie");

  vbox.getChildren().addAll(label, listView, button);

  button.setOnAction(action -> {
    int selection = listView.getSelectionModel().getSelectedIndex();
    recording(selection);            
    
    stage.close();
  });

  Scene scene = new Scene(vbox, 800, 400);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.show();
  
}

public static void copyMacro(){
  Stage stage = new Stage();
  stage.setTitle("Kopiowanie");
  VBox vbox = myVBox();

  Label label1 = new Label("Z:");
  ListView<String> listView1 = tasksListView();
  Label label2 = new Label("Do:");
  ListView<String> listView2 = tasksListView();
  Button button = new Button("Kopiuj");

  button.setOnAction(action -> {
    try{
      int index = listView1.getSelectionModel().getSelectedIndex();
      int destinationIndex = listView2.getSelectionModel().getSelectedIndex();
      tasks.getTask(destinationIndex ).copyMacro(tasks.getTask(index ).getMacro());
      displaySuccess("Pomyślnie skopiowano makro");
    } catch (NullPointerException ex){
      displayError("Makro, z którego chcesz dokonać kopiowania nie istnieje");
    }
    stage.close();
  });

  vbox.getChildren().addAll(label1, listView1, label2, listView2, button);

  Scene scene = new Scene(vbox, 800, 600);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.setScene(scene);
  stage.show();
}

public static void removeMacro(){
  Stage stage = new Stage();
  stage.setTitle("Usuwanie makra");
  VBox vbox = myVBox();

  Label label = new Label("Z którego pliku chcesz usunąć makro?:");
  ListView<String> listView = tasksListView();
  Button button = new Button("Usuń");

  vbox.getChildren().addAll(label, listView, button);

  button.setOnAction(action -> {
    int selection = listView.getSelectionModel().getSelectedIndex();
    tasks.getTask(selection ).removeMacro();

    stage.close();
  });

  Scene scene = new Scene(vbox, 800, 400);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.setScene(scene);
  stage.show();
}

public static void removeClickFromMacro(){
  Stage stage = new Stage();
  stage.setTitle("Usuwanie kliknięcia z makra");
  VBox vbox = myVBox();

  Label label = new Label("W którym pliku chcesz usunąć kliknięcie w makrze?");
  ListView<String> listView = tasksListView();
  Button button = new Button("Wybierz");

  button.setOnAction(action -> {
    int selection = listView.getSelectionModel().getSelectedIndex();
    if(tasks.getTask(selection ).getMacro().getIsSet()){
      deletingClick(selection);
      stage.close();
    }
    else {
      displayError("Makro nie istnieje");
      stage.close();
    }
  });

  vbox.getChildren().addAll(label, listView, button);

  Scene scene = new Scene(vbox, 800, 500);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.show();
}

public static void addClickToMacro(){ 
  Stage stage = new Stage();
  stage.setTitle("Dodawanie kliknięcia");

  VBox vbox = myVBox();

  Label label = new Label("W którym pliku chcesz dodać kliknięcie?");
  ListView<String> listView = tasksListView();
  Button button = new Button("Wybierz");

  button.setOnAction(action -> {
    int selection = listView.getSelectionModel().getSelectedIndex();
    addingClick(selection);
    
    stage.close();
  });

  vbox.getChildren().addAll(label, listView, button);

  Scene scene = new Scene(vbox, 800, 400);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.show();
}

public static void changeDelay(){
  Stage stage = new Stage();
  stage.setTitle("Zmiana opóźnienia");

  VBox vbox = myVBox();

  Label label = new Label("W którym pliku chcesz zmienić opóźnienie kliknięcia?");
  ListView<String> listView = tasksListView();
  Button button = new Button("Wybierz");

  button.setOnAction(action -> {
    int selection = listView.getSelectionModel().getSelectedIndex();
    if(!tasks.getTask(selection ).getMacro().getIsSet())
      displayError("Makro nie istnieje");
    else 
      changingDelay(selection);

    stage.close();
  });

  vbox.getChildren().addAll(label, listView, button);

  Scene scene = new Scene(vbox, 800, 500);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.show();
}

public static void printMacroClicksFromTask(){
  Stage stage = new Stage();
  stage.setTitle("Kliknięcia");

  VBox vbox = myVBox();

  Label label = new Label("Z którego pliku wypisać kliknięcia?");
  ListView<String> listView = tasksListView();
  Button button = new Button("Wybierz");

  button.setOnAction(action -> {
    int selection = listView.getSelectionModel().getSelectedIndex();
    if(tasks.getTask(selection ).getMacro().getIsSet()){
      printClicks(selection);
      stage.close();
    }
    else {
      displayError("Makro nie istnieje");
      stage.close();
    }
  });

  vbox.getChildren().addAll(label, listView, button);

  Scene scene = new Scene(vbox, 800, 500);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.show();

}

public static void printClicks(int index){
  Stage stage = new Stage();
  
  VBox vbox = myVBox();

  ListView<String> listView = clicksListView(index);

  vbox.getChildren().addAll(listView);

  Scene scene = new Scene(vbox, 800, 400);
  stage.setScene(scene);
  stage.show();
}

/* ----------------------------------------------- */

/* SETTINGS */
public static void changeSchedule(){
  Stage stage = new Stage();
  stage.setTitle("Zmienianie godziny");
  VBox vbox = myVBox();

  Label label = new Label("Podaj nową godzinę uruchamiania plików:");
  TextField textField = new TextField();
  Label labelNote = new Label("Aby doszło do zmiany musisz uruchomić ponownie program");

  textField.setOnAction(action -> {
    try{
      int time = Integer.valueOf(textField.getText());
      tasks.setHour(time);
      displaySuccess("Zmiana godziny uruchamiania powiodła się\n" + 
      "Pamiętaj o ponownym uruchomieniu programu");
    } catch (Exception e){
      displayError(e.getMessage());
    }

    stage.close();
  });

  vbox.getChildren().addAll(label, textField, labelNote);
  Scene scene = new Scene(vbox);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.show();
}

public static void printInstructions(){
  Stage stage = new Stage();
  stage.setTitle("Instrukcje pomocnicze");

  VBox vbox = myVBox();
  Label labelGeneral = new Label("Program pozwala na dodanie do niego ścieżek dostępu do plików, które\n" + 
    "będzie uruchamiać o wybranej przez ciebie godzinie (domyślnie o 24:00)\n" +
    "wraz z makrami (tutaj kliknięciami na ekran), które aby mialy możliwość\n" + 
    "zadzialać musimy wcześniej nagrać. Program musi być cały czas uruchomiony,\n" + 
    "aby pliki się uruchomiły o wybranej godzinie. Makro ma na celu\n" + 
    "odświeżenie pliku i jego sformatowanie do używalnej formy,\n" + 
    "uruchamia się wraz z plikiem do niego przypisanym.\n\n" + 
    "Można jednak użyć tego programu do innych celów");


  vbox.getChildren().addAll(labelGeneral);
  
  Scene scene = new Scene(vbox);
  stage.setScene(scene);
  stage.show();
}

/* ------------------------------------------------- */

public static void changingDelay(int index){
  Stage stage =  new Stage();
  stage.setTitle("Zmienianie opóźnienia");

  VBox vbox = myVBox();
  HBox hbox1 = myHBox();
  HBox hbox2 = myHBox();

  Label label = new Label("W którym kliknięciu chcesz zmienić opóźnienie?");
  ListView<String> listView = clicksListView(index);
  Label labelDelay = new Label("Opóźnienie w:");
  CheckBox checkBoxSeconds = new CheckBox("Sekundach");
  CheckBox checkBoxMinutes = new CheckBox("Minutach");
  Label labelScan = new Label("Podaj opóżnienie");
  TextField textField = new TextField();
  Button button = new Button("Zmień");

  button.setOnAction(action -> {
    try{
      int selection = listView.getSelectionModel().getSelectedIndex();
      int delay = Integer.valueOf(textField.getText());
      tasks.getTask(index ).getMacro().changeDelay(type, selection, delay);
      displaySuccess("Zmiana opóźnienia powiodła się");
    } catch (Exception ex) {
      displayError("Wystąpił błąd przy zmianie opóźnienia");
    }
    stage.close();
  });

  checkBoxMinutes.setOnAction(action -> {
    checkBoxSeconds.setSelected(false);
    type = 2;
  });
  checkBoxSeconds.setOnAction(action -> {
    checkBoxMinutes.setSelected(false);
    type = 1;
  });

  hbox2.getChildren().addAll(labelScan, textField);
  hbox1.getChildren().addAll(checkBoxSeconds, checkBoxMinutes);
  vbox.getChildren().addAll(label, listView, labelDelay, hbox1, hbox2, button);

  
  Scene scene = new Scene(vbox, 800, 600);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.show();
}

public static void addingClick(int taskIndex){
  Stage stage = new Stage();
  stage.setTitle("Dodawanie kliknięcia");
  VBox vbox = myVBox();
  HBox hbox1 = myHBox();
  HBox hbox2 = myHBox();

  Label label0 = new Label("Po którym kliknięciu dodać kliknięcie?");
  ListView<String> listView = clicksListView(taskIndex);
  Label label = new Label("Opóźnienie w:");
  CheckBox checkBoxSeconds = new CheckBox("Sekundach");
  CheckBox checkBoxMinutes = new CheckBox("Minutach");
  Label labelScan = new Label("Podaj opóżnienie");
  TextField textField = new TextField();
  Button buttonAdd = new Button("Dodaj kliknięcie");

  Macro macro = tasks.getTask(taskIndex ).getMacro();

  hbox2.getChildren().addAll(labelScan, textField);
  hbox1.getChildren().addAll(checkBoxSeconds, checkBoxMinutes);
  vbox.getChildren().addAll(label0, listView, label, hbox1, hbox2, buttonAdd);

  
  checkBoxMinutes.setOnAction(action -> {
    checkBoxSeconds.setSelected(false);
    type = 2;
  });
  checkBoxSeconds.setOnAction(action -> {
    checkBoxMinutes.setSelected(false);
    type = 1;
  });

  buttonAdd.setOnAction(action -> {
    try{
      int selection = listView.getSelectionModel().getSelectedIndex();
      int time = Integer.valueOf(textField.getText());
      if(!macro.addClick(type, time, selection))
        displayError("Nagranie kliknięcia nie powiodło się");
      else
        displaySuccess("Dodanie kliknięcia powiodło się");
      stage.close();
    } catch (Exception ex) {
      displayError("Nieprawidłowy czas");
    }
  });

  Scene scene = new Scene(vbox, 600, 600);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.setScene(scene);
  stage.show();
}

public static void deletingClick(int taskIndex){
  Stage stage = new Stage();
  stage.setTitle("Usuwanie");

  VBox vbox = myVBox();

  Label label = new Label("Które kliknięcie usunąć?");
  ListView<String> listView = clicksListView(taskIndex);
  Button button = new Button("Usuń");

  button.setOnAction(action -> {
    int clickIndex = listView.getSelectionModel().getSelectedIndex();

    try{
      tasks.getTask(taskIndex ).removeClickFromMacro(clickIndex);
      displaySuccess("Pomyślnie usunięto kliknięcie");
      stage.close();
    } catch(NullPointerException ex){
      displayError("Wystąpił błąd przy usuwaniu kliknięcia");
      stage.close();
    }
  });

  vbox.getChildren().addAll(label, listView, button);

  Scene scene = new Scene(vbox, 800, 400);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.show();
}

public static void recording(int index){
  Stage stage = new Stage();
  stage.setTitle("Nagrywanie/Dogrywanie");
  VBox vbox = myVBox();
  HBox hbox1 = myHBox();
  HBox hbox2 = myHBox();

  Label label = new Label("Opóźnienie w:");
  CheckBox checkBoxSeconds = new CheckBox("Sekundach");
  CheckBox checkBoxMinutes = new CheckBox("Minutach");
  Label labelScan = new Label("Podaj opóżnienie");
  TextField textField = new TextField();
  Button buttonNext = new Button("Dodaj następne kliknięcie");
  Button buttonFinish = new Button("Zakończ");

  Macro macro = tasks.getTask(index ).getMacro();

  hbox2.getChildren().addAll(labelScan, textField);
  hbox1.getChildren().addAll(checkBoxSeconds, checkBoxMinutes);
  vbox.getChildren().addAll(label, hbox1, hbox2, buttonNext, buttonFinish);

  
  checkBoxMinutes.setOnAction(action -> {
    checkBoxSeconds.setSelected(false);
    type = 2;
  });
  checkBoxSeconds.setOnAction(action -> {
    checkBoxMinutes.setSelected(false);
    type = 1;
  });

  buttonNext.setOnAction(action -> {
    try{
      int time = Integer.valueOf(textField.getText());
      if(!macro.recordMacro(type, time))
        displayError("Nagranie kliknięcia nie powiodło się");
    } catch (Exception ex) {
      displayError("Nieprawidłowy czas");
    }
  });

  buttonFinish.setOnAction((action -> {
    type = -1;
    displaySuccess("Pomyślnie nagrano makro");
    stage.close();
  }));

  Scene scene = new Scene(vbox);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.setScene(scene);
  stage.show();
}

public static void displayError(String message){
  Stage stage =  new Stage();
  stage.setTitle("Błąd");
  VBox vbox = myVBox();

  Label label = new Label(message);
  Button button  = new Button("OK");

  vbox.getChildren().addAll(label, button);

  button.setOnAction(action -> {
    stage.close();
  });
  Scene scene = new Scene(vbox);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.showAndWait();
}

public static void displaySuccess(String message){
  Stage stage =  new Stage();
  stage.setTitle("Sukces");
  VBox vbox = myVBox();

  Label label = new Label(message);
  Button button  = new Button("OK");

  vbox.getChildren().addAll(label, button);

  button.setOnAction(action -> {
    stage.close();
  });
  Scene scene = new Scene(vbox);
  stage.setScene(scene);
  stage.initModality(Modality.APPLICATION_MODAL);
  stage.showAndWait();
}

/* ------------------------------------------------- */

/* METHODS */

public static VBox myVBox(){
  VBox vbox = new VBox();
  vbox.setPadding(new Insets(15, 12, 15, 12));
  vbox.setSpacing(10);
  vbox.setStyle("-fx-background-color: #959595;");    
  return vbox;
}

public static HBox myHBox(){
  HBox hbox = new HBox();
  hbox.setPadding(new Insets(15, 12, 15, 12));
  hbox.setSpacing(10);
  hbox.setStyle("-fx-background-color: #959595;");    
  return hbox;
}

public static ListView<String> clicksListView(int index){
  ListView<String> listView = new ListView<>();
  
  if(tasks.getTask(index ).getMacro().getIsSet()){
    int i = 1;  
    for(ClickCord click : tasks.getTask(index ).getMacro().getClicks()){
      long delayInSeconds = click.getDelay()/1000;
      listView.getItems().add(i + ". Kordynaty: " + click.getX() + " x " + click.getY() + " - Opóźnienie: " + delayInSeconds + " sekund");
      i++;
    }
  }

  return listView;
}

public static ListView<String> tasksListView(){
  ListView<String> listView = new ListView<String>();
  for (Task task : tasks.getTasks()){
    listView.getItems().add(task.getFilePath() + "       Makro: " + isSetToString(task.getIsSet()) + " - Aktywność: " + isByppasedToString(task.getIsBypassed()));
  }
  return listView;
}

public static void printOptions(){
  System.out.println(
    "\n1. Uruchom pliki\n" + 
    "2. Uruchom wybrany plik\n...\n" + 
    "3. Zapisz dane (zapisuj regularnie na wypadek wystapienia bledu lub wylaczenia komputera)\n...\n" + 
    "4. Wylacz/wlacz otwieranie wybranego pliku\n" + 
    "5. Dodaj plik do uruchomienia\n" + 
    "6. Usun plik do uruchomienia\n...\n" + 
    "7. Nagraj makro\n" + 
    "8. Usun wybrane klikniecia z makra\n" + 
    "9. Dodaj klikniecia do makra w wybranym miejscu\n" +  
    "10. Zmien opoznienie przy wybranym kliknieciu\n" + 
    "11. Usun makro\n...\n" + 
    "12. Wypisz pliki\n" + 
    "13. Wypisz klikniecia w wybranym pliku i makrze\n" + 
    "14. Zmien godzine uruchamiania plikow\n" + 
    "15. Zmien sciezke do wybranego pliku\n" + 
    "16. Skopiuj makro z jednego pliku do drugiego\n...\n" + 
    "17. Zobacz instrukcje pomocnicze\n" + 
    "...\n" + 
    "0. Zapisz i zamknij program\n" + 
    "Wpisz liczbe przy opcji, ktora chesz wykonac");
}

public static Calendar setTime(){
  Calendar calendar = Calendar.getInstance();

  int currentHour = calendar.get(Calendar.HOUR_OF_DAY); 
  int currentMinute = calendar.get(Calendar.MINUTE);

  if (tasks.getHour() < currentHour){
    calendar.set(
      Calendar.DAY_OF_WEEK,
      calendar.get(Calendar.DAY_OF_WEEK) + 1
    );
    calendar.set(Calendar.HOUR_OF_DAY, tasks.getHour());
    calendar.set(Calendar.MINUTE, tasks.getMinute());
  } else if ( tasks.getHour() == currentHour){
    if (tasks.getMinute() <= currentMinute){
      calendar.set(
        Calendar.DAY_OF_WEEK,
        calendar.get(Calendar.DAY_OF_WEEK) + 1
      );
      calendar.set(Calendar.HOUR_OF_DAY, tasks.getHour());
      calendar.set(Calendar.MINUTE, tasks.getMinute());
    } else {
      calendar.set(
      Calendar.DAY_OF_WEEK,
      calendar.get(Calendar.DAY_OF_WEEK)
    );
    calendar.set(Calendar.HOUR_OF_DAY, tasks.getHour());
    calendar.set(Calendar.MINUTE, tasks.getMinute());
    }
  } else {
    calendar.set(
      Calendar.DAY_OF_WEEK,
      calendar.get(Calendar.DAY_OF_WEEK)
    );
    calendar.set(Calendar.HOUR_OF_DAY, tasks.getHour());
    calendar.set(Calendar.MINUTE, tasks.getMinute());
  }
  return calendar;
}

public static String isSetToString(boolean b){
  if(b){
    return "istnieje";
  } else {
    return "nie istnieje";
  }
}

public static String isByppasedToString(boolean b){
  if(!b){
    return "tak";
  } else {
    return "nie";
  }
}

/* ----------------------------------------------------------- */

/* FILE SAVING */

public static void saveTasks(){
  try{
    FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
    objectOutputStream.writeObject(tasks);
    objectOutputStream.flush();
    objectOutputStream.close();
    displaySuccess("Pomyślnie zapisano pliki");
  } catch (Exception ex){
    displayError(ex.getMessage());
  }
  
}

public static void loadTasks(){
    try {
      FileInputStream fileInputStream = new FileInputStream(saveFile);
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      tasks = (TasksList) objectInputStream.readObject();
      objectInputStream.close();
      displaySuccess("Pomyślnie wczytano pliki");
    } catch (FileNotFoundException e) {        
      displayError("Poprzedni zapis nie istnieje");
    } catch (Exception e){
      displayError("Wystąpił błąd " + e.getMessage());
    }
}

/* -------------------------------------------------------- */   

}