package me.vasylkov.steamparser.apache_client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LocalConnection extends ClientConnection {
    public LocalConnection() {
        super();
    }
}
