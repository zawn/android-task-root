package cn.mimessage.and.sdk.sdcard;

import java.util.HashMap;

import android.view.View;

public class MemoryViewHolder<T, E extends View> extends HashMap<String, View>
        implements IMemoryStore<View>
{
    private static final long serialVersionUID = 1L;

    @Override
    public void holdInMemory(java.lang.String name, View obj)
    {
        put(name, obj);
    }

    @Override
    public View getFromMemory(java.lang.String name)
    {
        return get(name);
    }

}
