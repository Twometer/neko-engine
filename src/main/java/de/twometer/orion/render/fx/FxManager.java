package de.twometer.orion.render.fx;

import de.twometer.orion.event.Events;
import de.twometer.orion.event.SizeChangedEvent;
import de.twometer.orion.util.Log;
import org.greenrobot.eventbus.Subscribe;

public class FxManager {

    private final Bloom bloom = new Bloom();

    private final SSAO ssao = new SSAO();

    public void create() {
        Log.d("Creating effects engine");
        Events.register(this);
        bloom.create();
        ssao.create();
    }

    @Subscribe
    public void onSizeChanged(SizeChangedEvent e) {
        var w = e.width;
        var h = e.height;
        bloom.resize(w, h);
        ssao.resize(w, h);
    }

    public Bloom getBloom() {
        return bloom;
    }

    public SSAO getSsao() {
        return ssao;
    }

}
