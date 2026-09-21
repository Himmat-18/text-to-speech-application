import { useEffect, useState } from "react";
import "./App.css";

function App() {
  const [text, setText] = useState("");
  const [language, setLanguage] = useState("English");
  const [voice, setVoice] = useState("");

  const [voices, setVoices] = useState({});
  const [loadingVoices, setLoadingVoices] = useState(true);

  const [loading, setLoading] = useState(false);
  const [downloading, setDownloading] = useState("");
  const [audioUrl, setAudioUrl] = useState("");
  const [error, setError] = useState("");

  const MAX_CHARACTERS = 500;

  const languageCodes = {
    English: "en-us",
    Hindi: "hi-in",
    Gujarati: "gu-in",
    Marathi: "mr-in",
    Spanish: "es-es",
    German: "de-de"
  };


  // ==========================================
  // LOAD VOICES
  // ==========================================

  useEffect(() => {
    const loadVoices = async () => {
      try {
        setLoadingVoices(true);

        const response = await fetch(
          "http://localhost:8080/api/voices"
        );

        if (!response.ok) {
          throw new Error(
            `Server error: ${response.status}`
          );
        }

        const data = await response.json();

        console.log(
          "Voices received from backend:",
          data
        );

        setVoices(data);

      } catch (error) {

        console.error(
          "Voice loading error:",
          error
        );

        setError(
          "Failed to load voices. Please make sure the backend is running."
        );

      } finally {

        setLoadingVoices(false);
      }
    };

    loadVoices();

  }, []);


  // ==========================================
  // GENERATE SPEECH
  // ==========================================

  const handleGenerate = async () => {

    setError("");

    if (!text.trim()) {

      setError(
        "Please enter some text!"
      );

      return;
    }

    if (text.length > MAX_CHARACTERS) {

      setError(
        `Maximum ${MAX_CHARACTERS} characters allowed!`
      );

      return;
    }

    if (!voice) {

      setError(
        "Please select a voice!"
      );

      return;
    }

    setLoading(true);

    try {

      const response = await fetch(
        "http://localhost:8080/api/tts",
        {
          method: "POST",

          headers: {
            "Content-Type": "application/json"
          },

          body: JSON.stringify({
            text: text,
            language: languageCodes[language],
            voice: voice,
            format: "mp3"
          })
        }
      );


      if (!response.ok) {

        let message =
          `Server error: ${response.status}`;

        try {

          const data =
            await response.json();

          if (data.error) {
            message = data.error;
          }

        } catch {

        }

        throw new Error(message);
      }


      const audioBlob =
        await response.blob();


      console.log(
        "Audio MIME type:",
        audioBlob.type
      );

      console.log(
        "Audio size:",
        audioBlob.size
      );

      console.log(
        "Selected voice:",
        voice
      );


      const url =
        URL.createObjectURL(
          audioBlob
        );


      setAudioUrl(url);


    } catch (error) {

      console.error(
        "Speech generation error:",
        error
      );

      setError(
        error.message ||
        "Failed to generate speech."
      );

    } finally {

      setLoading(false);
    }
  };


  // ==========================================
  // DOWNLOAD AUDIO
  // ==========================================

  const handleDownload = async (
    format
  ) => {

    setError("");

    if (!text.trim()) {

      setError(
        "Please enter some text!"
      );

      return;
    }

    if (!voice) {

      setError(
        "Please select a voice!"
      );

      return;
    }


    setDownloading(format);


    try {

      const response = await fetch(
        "http://localhost:8080/api/tts",
        {
          method: "POST",

          headers: {
            "Content-Type":
              "application/json"
          },

          body: JSON.stringify({
            text: text,
            language:
              languageCodes[language],
            voice: voice,
            format: format
          })
        }
      );


      if (!response.ok) {

        let message =
          `Download failed: ${response.status}`;

        try {

          const data =
            await response.json();

          if (data.error) {
            message = data.error;
          }

        } catch {

        }

        throw new Error(message);
      }


      const audioBlob =
        await response.blob();


      console.log(
        `${format.toUpperCase()} MIME type:`,
        audioBlob.type
      );

      console.log(
        `${format.toUpperCase()} size:`,
        audioBlob.size
      );


      const url =
        URL.createObjectURL(
          audioBlob
        );


      const link =
        document.createElement("a");

      link.href = url;

      link.download =
        `generated-speech.${format}`;

      document.body.appendChild(link);

      link.click();

      document.body.removeChild(link);

      URL.revokeObjectURL(url);


    } catch (error) {

      console.error(
        `${format} download error:`,
        error
      );

      setError(
        error.message ||
       `Failed to download ${format.toUpperCase()}.`
      );

    } finally {

      setDownloading("");
    }
  };


  // ==========================================
  // CLEAR
  // ==========================================

  const handleClear = () => {

    setText("");

    setLanguage("English");

    setVoice("");

    setAudioUrl("");

    setError("");
  };


  // ==========================================
  // LANGUAGE CHANGE
  // ==========================================

  const handleLanguageChange = (e) => {

    setLanguage(
      e.target.value
    );

    setVoice("");

    setAudioUrl("");

    setError("");
  };


  // ==========================================
  // UI
  // ==========================================

  return (
    <div className="app">

      <div className="container">

        <h1>
          Text To Speech
        </h1>


        {/* TEXT AREA */}

        <textarea
          placeholder="Enter your text here..."
          value={text}
          maxLength={MAX_CHARACTERS}
          onChange={(e) =>
            setText(e.target.value)
          }
        />


        {/* WORD + CHARACTER COUNT */}

        <div className="count">

          <span>
            Words:{" "}
            {text.trim() === ""
              ? 0
              : text
                  .trim()
                  .split(/\s+/)
                  .length}
          </span>

          <span>
            Characters:{" "}
            {text.length} /{" "}
            {MAX_CHARACTERS}
          </span>

        </div>


        {/* ERROR */}

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}


        {/* LANGUAGE + VOICE */}

        <div className="select-group">

          <div>

            <label>
              Language
            </label>

            <select
              value={language}
              onChange={
                handleLanguageChange
              }
            >

              <option value="English">
                English
              </option>

              <option value="Hindi">
                Hindi
              </option>

              <option value="Gujarati">
                Gujarati
              </option>

              <option value="Marathi">
                Marathi
              </option>

              <option value="Spanish">
                Spanish
              </option>

              <option value="German">
                German
              </option>

            </select>

          </div>


          <div>

            <label>
              Voice
            </label>

            <select
              value={voice}
              onChange={(e) =>
                setVoice(e.target.value)
              }
              disabled={loadingVoices}
            >

              <option value="">
                {loadingVoices
                  ? "Loading voices..."
                  : "Select Voice"}
              </option>


              {voices[language] &&
  Object.entries(voices[language]).map(
    ([voiceName, voiceId]) => (
      <option
        key={voiceId}
        value={voiceId}
      >
        {voiceName}
      </option>
    )
  )}
            </select>

          </div>

        </div>


        {/* MAIN BUTTONS */}

        <div className="button-group">

          <button
            className="clear-btn"
            onClick={handleClear}
          >
            🗑️ Clear
          </button>


          <button
            className="generate-btn"
            onClick={handleGenerate}
            disabled={loading}
          >

            {loading
              ? "Generating..."
              : "🔊 Generate Speech"}

          </button>

        </div>


        {/* AUD{voicesIO PLAYER */}

        {audioUrl && (

          <div className="audio-section">

            <h2>
              Generated Audio
            </h2>


            <audio
              key={audioUrl}
              controls
              src={audioUrl}
            >
              Your browser does not support
              audio.
            </audio>


            {/* DOWNLOAD BUTTONS */}

            <div className="download-buttons">

              <button
                className="download-btn"
                onClick={() =>
                  handleDownload("mp3")
                }
                disabled={
                  downloading !== ""
                }
              >

                {downloading === "mp3"
                  ? "Downloading..."
                  : "⬇️ Download MP3"}

              </button>


              <button
                className="download-btn"
                onClick={() =>
                  handleDownload("wav")
                }
                disabled={
                  downloading !== ""
                }
              >

                {downloading === "wav"
                  ? "Converting..."
                  : "⬇️ Download WAV"}

              </button>


              <button
                className="download-btn"
                onClick={() =>
                  handleDownload("ogg")
                }
                disabled={
                  downloading !== ""
                }
              >

                {downloading === "ogg"
                  ? "Converting..."
                  : "⬇️ Download OGG"}

              </button>

            </div>

          </div>

        )}

      </div>

    </div>
  );
}

export default App;