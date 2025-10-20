package hello.pet.authservice.application.port.out.result;

public record LogoutResult(boolean success, String message) {
    public static LogoutResult successLogout() {
        return new LogoutResult(true, "로그아웃이 성공적으로 완료 되었습니다.");
    }

    public static LogoutResult fail(String message) {
        return new LogoutResult(false, message);
    }
}
