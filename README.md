
# 📦 File Compressor & Decompressor  
**Java + Maven GUI Application using GZIP (DEFLATE) & XZ (LZMA2)**

---

## 📝 Overview  
This desktop tool allows users to **compress and decompress files** through a simple and intuitive GUI.  
It supports **two compression algorithms**, allowing the user to choose based on preference:

| Algorithm | Type | Strength | Speed | File Extension |
|----------|------|----------|--------|----------------|
| **GZIP (DEFLATE)** | Fast | Medium Compression | 🔥 Fast | `.gz` |
| **XZ (LZMA2)** | Advanced | **High Compression** | 🐢 Slower | `.xz` |

---

## ✨ Features

✔ Compress any file with **GZIP or XZ**  
✔ Decompress `.gz` & `.xz` automatically  
✔ UI shows list of compressed files with details:  
&nbsp;&nbsp;🔹 Original size  
&nbsp;&nbsp;🔹 Compressed size  
&nbsp;&nbsp;🔹 Compression ratio  
&nbsp;&nbsp;🔹 Output path  
✔ Open compressed file directly from GUI  
✔ Minimal clean Swing interface (intuitive & lightweight)  
✔ Maven project – dependencies managed automatically

---

## 🗂 Project Structure

```
📦 Compressor_Decompressor
 ├── pom.xml
 ├── README.md
 ├── Images/
 └── src/main/java/
     ├── comp_decomp/
     │   ├── CompressionType.java
     │   ├── compressor.java
     │   └── decompressor.java
     └── GUI/
         ├── AppFrame.java
         └── Main.java
```

---

## 🚀 How to Run

### 1️⃣ Using Maven CLI

```bash
mvn clean package
mvn exec:java -Dexec.mainClass="GUI.Main"
```

### 2️⃣ Using IDE (IntelliJ / VS Code / Eclipse)

```
Open project → wait for Maven to sync → run `GUI.Main`
```

---

## 🔧 Build as Executable JAR

```bash
mvn clean package
```
Output file will be stored inside:

```
target/CompressorDecompressor-1.0-SNAPSHOT.jar
```

---

## 📸 GUI Preview  


---

## 🏁 Future Enhancements (Optional)

| Feature | Status | Upgrade Idea |
|--------|--------|--------------|
| Batch compression | ❌ | Multi-select support |
| Progress bar | ❌ | Real-time compression progress |
| Drag & Drop UI | ❌ | Modern UX upgrade |
| Build exe app | ❌ | Convert to installer |

---

## 💡 Summary

Simple, lightweight desktop compressor with UI + dual compression algorithms  
→ GZIP for speed, XZ for max compression.

