# SuperC Price Tracker

---

## Project Overview

The SuperC Price Tracker is a robust application designed to help users secure the best deals on SuperC products. 
It provides a convenient way for users to monitor price fluctuations and receive timely notifications when their desired products become available at a target price.

---

## Features

* **Customizable Price Tracking:** Users can specify a maximum desired price for any SuperC product.
* **Automated Daily Monitoring:** The backend system automatically checks SuperC product prices daily for any specials or discounts.
* **Email Notifications:** Users receive automated email alerts when a monitored product's price drops below their set threshold, ensuring they don't miss out on favorable pricing.

---

## How It Works

1.  **User Input:** Users provide their email address, the specific SuperC product identifier, and the maximum price they are willing to pay for that product.
2.  **Daily Price Check:** Our backend system systematically queries SuperC product prices on a daily basis, looking for any ongoing sales or discounts.
3.  **Smart Notifications:** If the current price of a monitored product falls below the user's specified maximum price, an automated email notification is immediately dispatched to inform them of the reduced price, enabling them to make a timely purchase.

---

## Technologies Used

The SuperC Price Tracker is built with a modern and efficient technology stack:

* **Backend:** Developed with **Java** and the **Spring Framework**, ensuring a scalable, secure, and maintainable server-side application.
* **Email Notifications:** Utilizes **Google SMTP** for reliable and efficient delivery of all price alert emails.
* **Frontend:** Built with **JavaScript**, providing a dynamic, responsive, and intuitive user experience.

---

## Getting Started
To get a local copy of this project up and running, follow these simple steps:

Clone the Repository:
git clone https://github.com/OlegVasiliev89/Super.git

Navigate to the Project Directory:
cd Super

Open in IntelliJ IDEA:
Open IntelliJ IDEA.
Select File > Open... and navigate to the Super directory you just cloned.
IntelliJ IDEA should recognize it as a Maven, and import the necessary dependencies automatically.
Run the Backend Application:
Once the project is loaded and indexed in IntelliJ IDEA, locate the main application class. This is typically found in src/main/java/com/project/SuperC and will contain the main method with SpringApplication.run(...).
Right-click on this file in the project explorer.
Select Run 'YourApplicationNameApplication'.
The Spring Boot application server will start, usually on port 8080. You'll see logs in the IntelliJ console indicating that the application has started successfully.
Access the Frontend:
Once the backend is running, open your preferred web browser.
Navigate to: http://localhost:8080/

---

## Contact

For any inquiries or support, please feel free to reach out.
