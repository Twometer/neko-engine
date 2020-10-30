package de.twometer.neko.render.filter;

import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.render.shading.RenderPass;

public class RenderPassFilter implements IModelFilter {

    private RenderPass renderPass = RenderPass.All;

    @Override
    public boolean shouldRender(ModelPart part) {
        if (renderPass == RenderPass.All)
            return true;

        var mat = part.getMaterial();
        if (mat.isOpaque() && renderPass == RenderPass.Opaque)
            return true;
        else return !mat.isOpaque() && renderPass == RenderPass.Translucent;
    }

    @Override
    public void update() {

    }

    public void setRenderPass(RenderPass renderPass) {
        this.renderPass = renderPass;
    }

}
