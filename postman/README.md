# Postman tests

`bookedly-auth.postman_collection.json` checks sign-up, log-in and the protected `/me` endpoint.
Every request has automatic tests, so a full run tells you what works and what doesn't.

## Run it
1. Start the app (`BookedlyApplication` in IntelliJ, or `./mvnw spring-boot:run`).
2. In Postman: **Import** → pick this file.
3. Right-click the **Bookedly - Auth** collection → **Run collection** → **Run**.

The run order matters (sign up → log in → `/me`), because later requests reuse the email and token
saved by earlier ones. Each run signs up a new email, so you can run it as often as you like.

| Folder | Request | Expected |
|---|---|---|
| Register | Sign up a new user | 201, Bearer token, role `CUSTOMER`, no password in the response |
| Register | Same email again | 409 |
| Register | Invalid input | 400, with an error for `email`, `password` and `fullName` |
| Login | Correct credentials | 200, new token |
| Login | Wrong password | 401 |
| Login | Unknown email | 401, same message as wrong password |
| Protected: /me | With token | 200, the logged-in user |
| Protected: /me | No token | 401 |
| Protected: /me | Fake token | 401 |

The base URL is the `baseUrl` collection variable (default `http://localhost:8080`).
