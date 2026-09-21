Text-to-Speech Application

A full-stack Text-to-Speech application built using React.js, Spring Boot, Java, and ElevenLabs API.

The application converts user-entered text into speech and supports audio generation and download in MP3, WAV, and OGG formats.

Features

- Text-to-Speech conversion
- Maximum 500 characters validation
- Character and word count
- Language selection
- Voice selection
- MP3 audio generation
- WAV audio download
- OGG audio download
- Audio player with play/pause, seek and volume controls
- Clear text functionality
- Input validation
- Error handling
- REST APIs using Spring Boot
- ElevenLabs API integration
- CORS support
- Postman API testing

Technologies Used

Frontend

- React.js
- Vite
- JavaScript
- HTML
- CSS

Backend

- Java 21
- Spring Boot
- REST API
- Maven

Text-to-Speech

- ElevenLabs API
- FFmpeg for WAV and OGG audio conversion

API Testing

- Postman

Project Structure

text-to-speech-project/
│
├── src/
│   ├── App.jsx
│   └── ...
│
├── tts-backend/
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/
│   │               └── example/
│   │                   └── tts_backend/
│   ├── pom.xml
│   └── ...
│
├── package.json
├── vite.config.js
└── README.md

Installation and Setup

Prerequisites

Make sure the following software is installed:

- Java 21
- Node.js
- npm
- Maven
- FFmpeg
- Git

Backend Setup

1. Open the "tts-backend" folder.
2. Configure the ElevenLabs API key in the backend.
3. Start the Spring Boot application.

The backend will run on:

http://localhost:8080

Frontend Setup

1. Open the project root folder.
2. Install the required dependencies:

npm install

3. Start the React development server:

npm run dev

The frontend will run on:

http://localhost:5173

API Documentation

1. Health Check

GET

/api/health

Checks whether the backend application is running.

2. Get Voices

GET

/api/voices

Returns the available voices used by the application.

3. Generate Speech

POST

/api/tts

Converts the provided text into speech.

Request Body

{
  "text": "Hello, welcome to the Text-to-Speech application.",
  "language": "English",
  "voice": "Female"
}

Response

Returns generated audio data in MP3 format.

Audio Formats

The application supports:

- MP3
- WAV
- OGG

FFmpeg is used to convert the generated audio into WAV and OGG formats.

Validation

The application validates user input before generating speech.

- Maximum text length: 500 characters
- Empty text is not allowed
- Character count is displayed
- Word count is displayed
- Language selection is required
- Voice selection is required

Error Handling

The application handles:

- Empty text input
- Character limit errors
- Invalid language or voice selection
- API errors
- Backend connection errors
- Audio generation errors

Supported Languages

The application includes voice options for:

- English
- Hindi
- Gujarati
- Marathi
- Spanish
- German

«Note: German voice generation may require a paid ElevenLabs plan depending on the selected voice/API availability.»

Running the Project

Start Backend

Run the Spring Boot application from IntelliJ IDEA or Maven.

Start Frontend

From the project root:

npm run dev

Then open:

http://localhost:5173

API Testing

The APIs were tested using Postman.

The following endpoints were tested:

- "GET /api/health"
- "GET /api/voices"
- "POST /api/tts"

Security

The ElevenLabs API key is configured on the backend and is not exposed directly in the React frontend.

Do not commit the API key to GitHub.

Future Improvements

- User authentication
- Voice preview
- More language and voice options
- Cloud deployment
- Database integration
- User audio history

Author

Himmat Singh Yadav