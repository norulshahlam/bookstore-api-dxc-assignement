package com.dxc.bookstoreapi.model.response;

import com.dxc.bookstoreapi.model.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookResponse<T> {

    @NonNull
    ResponseStatus status;
    T data;
    String errorMessage;

    public static <T> BookResponse successResponse(T data) {
        return BookResponse.builder()
                .status(ResponseStatus.SUCCESS)
                .data(data)
                .build();
    }
    public static BookResponse failureResponse(String errorMessage) {
        return BookResponse.builder()
                .status(ResponseStatus.FAILURE)
                .errorMessage(errorMessage)
                .build();
    }
}
