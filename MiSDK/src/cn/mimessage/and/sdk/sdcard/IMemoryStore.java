package cn.mimessage.and.sdk.sdcard;

public interface IMemoryStore<T>
{
    public void holdInMemory(String name, T obj);

    public T getFromMemory(String name);
}
