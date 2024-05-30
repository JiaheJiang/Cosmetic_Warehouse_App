# My Personal Project - A system for cosmetics sales warehouse

A cosmetics sales warehouse. Input information of warehouse's recently purchased cosmetic products. Possible features
include: cosmetics from various brands and with different types; a list of all confirmed cosmetics available for
sale; display of cosmetic pictures or a roster of their effects. (X can represent brand names,
Y can be a list of categories (e.g., "cleanser"), Z can be a list of customer preferences, etc.).

The warehouse owner would utilize it to track the input and output of cosmetics. Moreover, this system can assist the
owner in selling the products more effectively, enhancing the shopping experience for customers by enabling the owner
to make more accurate product sales. I found it's interesting to me since I purchased cosmetics frequently in my daily
life and I would like to make it easier to buy the products I need.

## Organize the products and make selling easier

### User Story

- As a user, I want to be able to add nearly purchased products to the warehouse.
- As a user, I want to be able to remove nearly sold products from the warehouse.
- As a user, I want to be able to see the list of available cosmetics products with their types.
- As a user, I want to be able to greet the customer come to my warehouse first and 
  match the customer's need with the type of cosmetics products, 
  then return the first product that matches needed type with its brand.
- As a user, when I select the quit option from the application menu, 
  I want to be reminded to save my present cosmetics list in my warehouse to file and 
  have the option to do so or not.
- As a user, when I start the application, I want to be given the option to load 
  my warehouse cosmetic products' list from file.

### Instructions for Grader:
* You can generate the first required action related to the user story "adding multiple Xs to a Y" by texting cosmetic
product's type into left textfield, and it will show in the combo box at right.
In the warehouse interface, the user will find that the right list shows the current warehouse products. On the left 
side, I created a textfield for the user to type in the cosmetic product they want to put into the warehouse. However, 
the field only accepts the user to type in the cosmetic product's type, not the brand name.

* You can generate the second required action related to the user story "purchasing multiple Xs in a Y" by clicking 
"purchase cosmetic products" button in the warehouse application. Upon clicking the button, a message window will 
appear, indicating the number of products you have purchased. The item purchased each time will be the current product 
displayed in the combo box.

* You can locate my visual component in the login interface. The background image is imported.

* You can save the state of my application by clicking "save current warehouse state" button. There is a "save button" 
on the right side of the warehouse app. If the user wants to save the current warehouse state, they can simply click the 
save button, and the app will save the state for them. 

* You can reload the state of my application by choosing "yes" when the pop-up window is shown afet you enter the 
correct username and password in the login interface. After the user successfully logs into the warehouse app, there 
will be a message asking whether they want to load the last warehouse state. If the user chooses "yes," the saved 
warehouse state will be displayed. If they choose "no," the app will open with the initialized warehouse state.

### Phase 4: Task 2
Thu Nov 30 20:36:34 PST 2023 
Load the last saved warehouse state. 

Thu Nov 30 20:36:41 PST 2023
Add a new cosmetic product: Lamer cream to the warehouse.

Thu Nov 30 20:36:56 PST 2023
Add a new cosmetic product: DW foundation to the warehouse.

Thu Nov 30 20:37:05 PST 2023
Remove the cosmetic product: Eyeliner from the warehouse.

Thu Nov 30 20:37:09 PST 2023
Remove the cosmetic product: Blush from the warehouse.

Thu Nov 30 20:37:16 PST 2023
Purchase the cosmetic product: Moisturizers from the warehouse.

Thu Nov 30 20:37:21 PST 2023
Purchase the cosmetic product: Cleansers from the warehouse.

Thu Nov 30 20:37:25 PST 2023
Save the current warehouse state.

### Phase 4: Task 3
To make further improvements to my CosmeticGUI, I propose two possible enhancements. The first one involves considering
a more robust exception handling approach. For instance, in the method addCosmetic, I can throw a 
InvalidCosmeticInputException when encountering invalid or unexpected input. This would not only offer clearer feedback
to users but also enhance the overall robustness of my code.

Secondly, I could enhance the interface's appeal by incorporating background music. This addition aims to elevate user 
satisfaction by creating a more immersive environment during interactions with CosmeticGUI. The inclusion of background 
music can contribute to a more enjoyable user experience and foster a comfortable atmosphere throughout the interaction.