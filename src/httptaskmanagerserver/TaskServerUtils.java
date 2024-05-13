package httptaskmanagerserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskServerUtils {
    public static Gson getGson() {
        class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            @Override
            public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
                jsonWriter.value(localDateTime.format(dateTimeFormatter));
            }

            @Override
            public LocalDateTime read(final JsonReader jsonReader) throws IOException {
                return LocalDateTime.parse(jsonReader.nextString(), dateTimeFormatter);
            }
        }

        class DurationAdapter extends TypeAdapter<Duration> {

            @Override
            public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
                jsonWriter.value(duration.toMinutes());
            }

            @Override
            public Duration read(final JsonReader jsonReader) throws IOException {
                return Duration.ofMinutes(jsonReader.nextLong());
            }
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        TypeAdapter<LocalDateTime> localDateTimeAdapter = new LocalDateTimeAdapter();
        TypeAdapter<LocalDateTime> safeLocalDateTimeAdapter = localDateTimeAdapter.nullSafe();
        TypeAdapter<Duration> durationAdapter = new DurationAdapter();
        TypeAdapter<Duration> safeDurationTimeAdapter = durationAdapter.nullSafe();

        gsonBuilder.serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, safeLocalDateTimeAdapter)
                .registerTypeAdapter(Duration.class, safeDurationTimeAdapter);
        return gsonBuilder.create();
    }

    public static class TaskListTypeToken extends TypeToken<List<Task>> {
    }

    public static class EpicListTypeToken extends TypeToken<List<Epic>> {
    }

    public static class SubtaskListTypeToken extends TypeToken<List<Subtask>> {
    }
}
