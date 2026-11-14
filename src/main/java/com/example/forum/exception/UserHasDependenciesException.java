package com.example.forum.exception;

public class UserHasDependenciesException extends IllegalStateException {
    public UserHasDependenciesException(Long userId, long threads, long posts, long comments) {
        super("Невозможно удалить пользователя с ID " + userId
                + ": обнаружены связанные материалы (треды — " + threads
                + ", посты — " + posts + ", комментарии — " + comments + "). "
                + "Связанный контент не был удалён.");
    }
}
