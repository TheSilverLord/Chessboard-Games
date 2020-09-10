import java.io.Serializable;

public class Request implements Serializable
{
    int operationID;
    // 0 - подключение нового клиента и передача ему данных о клиентах
    // 1 - передача данных клиентам о новом клиенте
    // 2 - отключение клиента
    // 3 - передача данных между клиентами
    // 4 - подключение клиентов друг к другу
    Object data;
    String receiver;

    Request(int ID)
    {
        this.operationID = ID;
    }

    Request(int ID, Object data)
    {
        this.operationID = ID;
        this.data = data;
    }

    Request(int ID, Object data, String receiver)
    {
        this.operationID = ID;
        this.data = data;
        this.receiver = receiver;
    }
}

class DataPack implements Serializable
{
    String source;
    String destination;

    DataPack(String source, String destination)
    {
        this.source = source;
        this.destination = destination;
    }
}