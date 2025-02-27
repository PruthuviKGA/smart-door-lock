# B20_Group16 - Smart Door Lock System with Fingerprint Authentication

## Project Overview  
The **Smart Fingerprint-Based Door Lock System** is a secure and efficient access control system that integrates **biometric authentication with IoT technology**. This project enhances security by allowing only registered users to access a door, locker, or restricted area using **fingerprint authentication**.  

The system consists of an **Arduino Uno**, a **Fingerprint Sensor (R503)**, a **Solenoid Lock**, an **HC-06 Bluetooth Module**, LEDs, and a Buzzer. It is designed to be user-friendly, with a **mobile application** enabling user registration and admin access.  

## Functionality  
- **User Registration:**  
  - Users register their details and add their fingerprint via a **mobile application**.  
  - The **admin** can log in to **view access logs and manage users**.  

- **Access Control:**  
  - When a **registered user** scans their fingerprint:  
    - The **solenoid lock unlocks**.  
    - A **green LED lights up**, indicating successful authentication.  
  - When an **unregistered user** attempts access:  
    - The **buzzer activates** as an alert.  
    - A **red LED lights up**, indicating unauthorized access.  

## Components Used  
### Hardware  
- **Arduino Uno** – Microcontroller to control the system.  
- **R503 Fingerprint Sensor** – Captures and verifies fingerprints.  
- **Solenoid Lock** – Locks and unlocks the door based on authentication.  
- **HC-06 Bluetooth Module** – Facilitates communication between the Arduino and the mobile app.  
- **LEDs (Green & Red)** – Indicate access status.  
- **Buzzer** – Alerts for unauthorized access attempts.  

### Software  
- **Mobile App** – Built using **Java**, enabling user registration and admin access.  

## Applications  
This system can be used in various real-world applications, such as:  
✔ **Smart home security systems**  
✔ **Office or restricted area access control**  
✔ **Secure lockers and cabinets**  

## Circuit Diagram  
![Circuit Diagram](Circuit_Diagram.jpg)

## Block Diagram  
*(To be added – Include a high-level block diagram showing system components.)*  

## Contributors  
This project was developed by **Group 16** from **Batch 20**:  

| Student ID | Name |  
|------------|-------------------------|  
| 204104H | Kularathna M.D.S.A. |  
| 204137K | Nethmini S.A.R. |  
| 204041K | Dilshan K.G.A.P. |  
| 204064H | Gunasiri G.C.S. |  
| 204244K | Jayasinghe N.D. |  
