# Orion Engine
LWJGL 3D game engine

## Features
- Deferred rendering pipeline
- Integrated [Ultralight](https://ultralig.ht) GUI framework
- Integrated Assimp model loader
- Post processing
  - SSAO
  - Bloom
  - Vignette
  - FXAA
  
## Engine setup
1. Import the latest jar file from the Maven repo
2. Download base assets from [here](https://github.com/Twometer/orion-engine/releases/latest) and put the assets folder in your working directory
3. Download the Ultralight binaries from [here](https://github.com/ultralight-ux/Ultralight/blob/master/README.md#getting-the-latest-sdk) and put them into `assets/Natives/Ultralight`.
  
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
  
## Screenshots
tbd

## Examples
tbd
