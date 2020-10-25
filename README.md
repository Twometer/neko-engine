# Neko Engine
LWJGL-based 3D game/rendering engine

## Features
- Deferred rendering pipeline
- Integrated [Ultralight](https://ultralig.ht) GUI framework
  - HTML GUIs
  - Custom CSS styles
  - Easy JS/Java interop
  - I18n support
- Integrated Assimp model loader
- Integrated OpenAL 3D sound engine
- Post processing effects
  - SSAO
  - Bloom
  - Vignette
  - FXAA
  
## Engine setup
1. Import the latest jar file from the Maven repo
```
repositories {
    ...
    maven { url 'https://cloud.twometer.de/nexus/repository/maven-releases' }
    ...
}

implementation 'de.twometer.neko:neko-engine:<version-here>'

```
2. Download base assets from [here](https://github.com/Twometer/neko-engine/releases/latest) and put the assets folder in your working directory
3. Download the Ultralight binaries from [here](https://github.com/ultralight-ux/Ultralight/blob/master/README.md#getting-the-latest-sdk) and put them into `assets/Natives/Ultralight`.
  
## Examples
Example code can be found [here](https://github.com/Twometer/neko-engine/tree/main/src/main/java/example)

For an actual game made with Neko, see my [Among Us 3D](https://github.com/Twometer/among-us-3d)
  
## Asset folder structure
- `assets/`
  - `Gui/`: Base directory for Ultralight. Store your UI files here.
  - `Models/`: Model files (OBJ, FBX, etc.); supports all types supported by Assimp
  - `Natives/`: Native library directory. 
  - `Shaders/`: All your GLSL shaders.
  - `Sounds/`: All the sounds for your game. Only supports OGG Mono (OpenAL limitation).
  - `Strings/`: Language files. Format: `<iso-code>.properties`, ex: "en-US.properties"
  - `Textures/`: Texture files for your models, and everything else.
  
## Used libraries
- [LWJGL](https://www.lwjgl.org/)
- [STB](https://github.com/nothings/stb)
- [Assimp](https://www.assimp.org/)
- [JOML](https://github.com/JOML-CI/JOML)
- [EventBus](https://github.com/greenrobot/EventBus)
- [Ultralight](https://ultralig.ht)
- [ultralight-java](https://github.com/LabyMod/ultralight-java)
- [kryonet](https://github.com/EsotericSoftware/kryonet)
- [GSON](https://github.com/google/gson)