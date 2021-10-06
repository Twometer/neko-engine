# Neko Engine
The Neko Engine is a 3D game engine using Kotlin and LWJGL

## Features

- [x] Deferred HDR rendering pipeline
- [x] Scenegraph-based model system
    - [x] Assimp model importer
    - [x] Bone and animation support
    - [x] Component system
- [x] Shader preprocessor: NekoShaders (.nks)
- [x] Ultralight UI and ImGUI
- [x] OpenAL 3D sound engine
- [x] 3D text billboards
- [x] Extensible Post Processing FX pipeline
    - [x] Scalable Ambient Occlusion
    - [x] Screen Space Reflections
    - [x] Bloom
    - [x] FXAA
    - [x] Vignette

## Usage

```groovy
repositories {
    maven { url 'https://maven.twometer.de/releases/' }
}

dependencies {
    implementation 'de.twometer:neko-engine:2.0.0'
}
```

## Examples

Example code can be found [here](https://github.com/nekotec/neko-engine/tree/main/neko-demo)

For a game made with the first version of neko engine, see my [Among Us 3D](https://github.com/Twometer/among-us-3d)

## Debugging

For finding bugs, crashes, OpenGL errors, memory leaks, etc. that occur with the Neko Engine or any games written with
it, I recommend the [LWJGLX Debugger](https://github.com/LWJGLX/debug). For finding performance issues, you can use the
built-in profiler.

## Licensing

The engine itself is licensed under the Apache-2.0 License. However, for displaying HTML-based GUIs, the engine requires
the [Ultralight](https://ultralig.ht) library, which has a proprietary licensing model. It is free to use for
non-commercial or low-revenue projects, but others may require a paid license.
See [their website](https://ultralig.ht/#pricing) for more information.

## Used libraries

- `org.lwjgl:lwjgl` (`assimp`, `glfw`, `meow`, `openal`, `opengl`, `stb`)
- `org.joml:joml`
- `org.slf4j:slf4j`
- `org.greenrobot:eventbus`
- `com.labymedia:ultralight-java`
- `io.github.spair:imgui-java`
- `io.github.microutils:kotlin-logging`

Some shaders are derived from shaders found online. Their sources are attributed in their respective files.

---
This is the 2.x branch. If you are interested in the the 1.x version,
see [here](https://github.com/nekotec/neko-engine/tree/1.x)