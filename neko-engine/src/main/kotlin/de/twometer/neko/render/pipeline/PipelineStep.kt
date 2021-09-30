package de.twometer.neko.render.pipeline

import de.twometer.neko.render.EffectsPipeline

abstract class PipelineStep {

    var active = true

    abstract fun render(pipeline: EffectsPipeline)

}