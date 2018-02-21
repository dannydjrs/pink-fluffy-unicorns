import java.util.*;

import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.scene.image.*;
import javafx.scene.control.Separator;

public class Interface extends Application {
// width and height of application
public static final int WIDTH = 500;
public static final int HEIGHT = 700;

Stage window;
boolean listening = false;
ArrayList<Message> queries;

VBox root;
VBox chatbot;
HBox textInput;
HBox buttons;
Label update_time;
TextField textField;

public void changeUpdateTime(String time) {
				update_time.setText("Last updated: " + time);
}

public void startListening() {

}

public void stopListening() {
	String query = "";
	// makeQuery(query);
}

public void makeQuery(String query) {
				addMessage(new Query(query));
				// process message here
				addMessage(new Response("Random response", null));
}

// start the gui
@Override
public void start(Stage primaryStage) throws Exception {
				window = primaryStage;
				init(window);

				root = new VBox(10);
				root.setId("root");
				root.setPadding(new Insets(20, 10, 20, 10));

				// chatbot container in scrollable pane, scrolled to bottom
				makeChatbotContainer();
				ScrollPane scroll = new ScrollPane(chatbot);
				scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
				scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
				scroll.setFitToWidth(true);

				// text input area for user to input queries
				makeTextInput();

				// control buttons
				makeButtons();

				root.getChildren().addAll(scroll, textInput, buttons);

				Scene scene = new Scene(root, WIDTH, HEIGHT);
				scene.getStylesheets().add("styles/stylesheet.css");
				window.setScene(scene);
				window.show();
}

public void addMessage(Message message) {
				queries.add(message);

				// container of message
				HBox container = new HBox(0);

				// fill one side of message to decrease it's size
				Region region = new Region();
				HBox.setHgrow(region, Priority.ALWAYS);

				// contains the content in the message
				VBox contentContain = new VBox(5);
				contentContain.getStyleClass().add("chat_bubble");

				// the main label for the message
				Label label = new Label(message.getMessage());
				label.setWrapText(true);

				// set specifics of containers depending on query or response
				if(message instanceof Query) {
								// if a query
								container.setPadding(new Insets(0,0,0,80));
								contentContain.setAlignment(Pos.CENTER_RIGHT);

								label.setTextAlignment(TextAlignment.RIGHT);
								contentContain.setId("query_send");
								contentContain.getChildren().add(label);

								container.getChildren().addAll(region, contentContain);
				} else if(message instanceof Response) {
								// if a response
								container.setPadding(new Insets(0,80,0,0));
								contentContain.setAlignment(Pos.CENTER_LEFT);

								label.setTextAlignment(TextAlignment.LEFT);
								contentContain.setId("query_recieve");
								contentContain.getChildren().add(label);

								// if the response contains news to display
								News[] news = message.getNews();
								if(news != null) {
												// set to max width for message
												contentContain.setPrefWidth(WIDTH);

												for(int x = 0; x < 3 && x < news.length; x++) {
																// container for the news displayed
																VBox newsContain = new VBox(5);
																newsContain.setAlignment(Pos.CENTER_RIGHT);

																// horizontal separator
																Separator separator = new Separator();
																separator.setMaxWidth(200);

																Label heading = new Label(news[x].getTitle());
																heading.setId("news_heading");
																newsContain.getChildren().addAll(separator, heading);

																contentContain.getChildren().add(newsContain);
												}

								}

								container.getChildren().addAll(contentContain, region);

				}
				chatbot.getChildren().add(container);
}

// make the chatbot
private void makeChatbotContainer() {
				// container for the chatbot i.e the messages and replies
				chatbot = new VBox(20);
				chatbot.setId("chatbot_container");
				chatbot.setPrefHeight(HEIGHT);
				chatbot.setPrefWidth(WIDTH);
				chatbot.setAlignment(Pos.TOP_CENTER);
}

private void makeTextInput() {
				textInput = new HBox(0);
				textInput.setAlignment(Pos.BOTTOM_CENTER);

				textField = new TextField();
				textField.setId("text_field");
				textField.setPrefWidth(WIDTH);
				textField.setPromptText("Input a query here");

				Button confirm = new Button("Send");
				confirm.setOnAction(e->{
												makeQuery(textField.getText());
												textField.setText("");
								});
				confirm.setPrefWidth(200);
				confirm.setId("send_query_button");

				textInput.getChildren().addAll(textField, confirm);
}

private void makeButtons() {
				buttons = new HBox(5);
				buttons.setAlignment(Pos.BOTTOM_RIGHT);

				Button helpButton = new Button();
				helpButton.setText("?");
				helpButton.setId("help_button");

				Region filler1 = new Region();
				HBox.setHgrow(filler1, Priority.ALWAYS);

				StackPane button = new StackPane();
				Image image = new Image(getClass().getResourceAsStream("images/microphone.png"));
				ImageView imageView = new ImageView(image);
				imageView.setPreserveRatio(false);
				imageView.setFitHeight(45);
				imageView.setFitWidth(34);

				Button roundButton = new Button("",imageView);
				roundButton.setId("round_button");
				roundButton.getStyleClass().add("not_listening");
				roundButton.setOnAction(e->{
												queries.add(new Query("..."));
												if(!listening) {
												        listening = true;
												        roundButton.getStyleClass().remove("not_listening");
												        roundButton.getStyleClass().add("listening");
												        startListening();
												} else {
												        listening = false;
												        roundButton.getStyleClass().remove("listening");
												        roundButton.getStyleClass().add("not_listening");
												        stopListening();
												}
								});
				button.getChildren().add(roundButton);

				update_time = new Label("Last updated: ...");
				update_time.setId("update_time");

				buttons.getChildren().addAll(helpButton, filler1, button, update_time);
}

private void init(Stage window) {
				window.setTitle("Trader's Assistant");
				window.setResizable(false);
				window.setOnCloseRequest(e->{
												e.consume(); // stop window closing automatically
												closeProgram();
								});
				queries = new ArrayList<Message>();
}

private void closeProgram() {

}

}
