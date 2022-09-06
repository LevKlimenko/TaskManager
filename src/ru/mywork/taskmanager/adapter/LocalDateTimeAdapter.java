package ru.mywork.taskmanager.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.ms");

    public  void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException{
        if (localDateTime == null){
            jsonWriter.value("null");
        }else {
            jsonWriter.value(localDateTime.format(formatter));
        }
    }

    public  LocalDateTime read(JsonReader jsonReader) throws IOException{
        final String text = jsonReader.nextString();
        if (text.equals("null")){
            return null;
        }
        return  LocalDateTime.parse(text,formatter);
    }
}
