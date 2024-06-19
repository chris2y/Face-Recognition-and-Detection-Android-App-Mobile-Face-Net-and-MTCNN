# FaceRecogApp

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=white)
![MobileFaceNet](https://img.shields.io/badge/MobileFaceNet-FF5722?style=for-the-badge&logo=none&logoColor=white)
![MTCNN](https://img.shields.io/badge/MTCNN-607D8B?style=for-the-badge&logo=none&logoColor=white)

## Description

FaceRecogApp is a robust Android application designed to detect and recognize faces using MobileFaceNet and MTCNN. It leverages Firebase for storing user details and vectors representing faces. When a face is detected, the app retrieves and displays the associated social media profiles, allowing users to click and redirect to these profiles. If a face is not recognized, the app provides a social sharing feature within the app to seek help from other users.

## Features

- **Face Detection and Recognition:** Utilizes MTCNN for face detection and MobileFaceNet for face recognition.
- **Vector Storage:** Saves face vectors to Firebase for easy retrieval and recognition.
- **Social Media Integration:** Stores and displays user details including YouTube, Twitter, etc.
- **User Interaction:** Allows users to click on recognized faces to redirect to their social media accounts.
- **Community Sharing:** Shares unidentified faces within the app's social platform for community assistance.
- **High Accuracy:** Provides highly accurate face recognition performance.

## Screenshots
<img src="https://github.com/chris2y/Face-Recognition-and-Detection-Android-App-Mobile-Face-Net-and-MTCNN/assets/105220772/31460247-b0ff-4729-9802-536b60d5c707" alt="Screenshot 2" width="300"/>
<img src="https://github.com/chris2y/Face-Recognition-and-Detection-Android-App-Mobile-Face-Net-and-MTCNN/assets/105220772/e9a0ff97-ff24-4a5e-93c7-848befe413db" alt="Screenshot 2" width="300"/>
<img src="https://github.com/chris2y/Face-Recognition-and-Detection-Android-App-Mobile-Face-Net-and-MTCNN/assets/105220772/6ea5d9d4-50a6-4e83-acc8-9c38d55b8941" alt="Screenshot 4" width="300"/>
<img src="https://github.com/chris2y/Face-Recognition-and-Detection-Android-App-Mobile-Face-Net-and-MTCNN/assets/105220772/dadd78c1-45b8-44b2-acca-d98ef2cdfeec" alt="Screenshot 3" width="300"/>


## Installation

To run this project, follow these steps:

1. **Clone the repository:**

    ```sh
    git clone https://github.com/chris2y/FaceRecogApp.git
    ```

2. **Open the project in Android Studio:**

    - Open Android Studio
    - Select `Open an existing Android Studio project`
    - Navigate to the cloned directory and select it

3. **Build and run the project:**

    - Click on the `Run` button in Android Studio to build and run the project on an emulator or a physical device

## Usage

- **Detect Faces:** Point the camera at a face to detect and recognize it.
- **View Social Media Profiles:** Click on recognized faces to view their associated social media profiles.
- **Store New Faces:** Add new faces by saving their details and vectors in Firebase.
- **Share Unrecognized Faces:** Share unrecognized faces in the app's social platform to get help from other users.
- **Navigate to Profiles:** Click on social media icons to redirect to the respective profiles.

## Contributing

Contributions are welcome! Follow these steps to contribute:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Open a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For any inquiries or feedback, please reach out to:

- **Email:** fissehachristian@gmail.com
- **GitHub:** [chris2y](https://github.com/chris2y)

---
