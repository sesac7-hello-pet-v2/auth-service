package hello.pet.authservice.adapter.out.dto;

public record LoginValidationResponse(
        boolean valid,
        String email,
        Long id,
        String nickname,
        String role,
        String profileUrl,
        String reason) {
}
