*Project Kronslott*
# DropWizard session based authentication
## Why use this bundle?
If you are building a single-page application with DropWizard as a backend, 
one of the options to stay authenticated is to keep the currently signed in
user in the session. Another solution would be to use basic authentication.

## Usage

### example.yml

This example configuration shows the default values.

```yaml
session:
  maxInactiveIntervalSeconds: -1
  httpOnly: true
  secure: true
```

Comments:
- You should really set maxInactiveIntervalSeconds to a suitable value
- If you can think of a reason to set httpOnly to false, please tell me about it
- In production you always use SSL and secure is set to true.
If you don't use SSL in your development environment you will need to turn it off there.


### example.kt
 
See [IntegrationTest.kt](https://github.com/twogood/kronslott-session-auth/blob/master/src/test/kotlin/se/activout/kronslott/auth/session/IntegrationTest.kt)
for a tiny working application using this library.

## Adding this library to your project

This project is not yet available from Maven Central Repository, but it's 
available via
[JitPack.io](https://jitpack.io/#se.activout/kronslott-session-auth).

[![Release](https://jitpack.io/v/se.activout/kronslott-session-auth.svg)]
(https://jitpack.io/#se.activout/kronslott-session-auth)
