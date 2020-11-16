# Firebase Authentication demo
Bewust niet refactored om de stappen onder elkaar te laten zien op de plek van actie

## voorwaarden

- ```google-services.json``` in app folder
- permissie internet in ```AndroidManifest.xml``` op root-niveau

```xml
    <uses-permission android:name="android.permission.INTERNET" />
```

## klassen

- FirebaseAuth
  - .getInstance()
  - signInWithCredential()
  - .signOut()

## Voorbeeld met GoogleSignin

LoginActivity navigeert na login naar MainActivity:Homefragment en laat Firebase User naam zien
Op HomeFragment een loguit-knop die na uitloggen/disconnect van Google naar LoginActivity navigeert

## klassen
-  GoogleSignInClient
   -  .signInIntent
-  GoogleSignInOptions
   -  .Builder
-  GoogleSignIn
   -  .getSignedInAccountFromIntent(data) // haalt uit Intent van GoogleSignIn activity result een GoogleSignInAccount
- GoogleSignInAccount
  - idToken om met GoogleAuthProvider.getCredential(...) AuthCredential om te halen
- GoogleAuthProvider
  - .getCredential
- AuthCredential wordt gebruikt door FirebaseAuth om in te loggen

## TODO

FirebaseUI


