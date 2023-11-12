# Whisker
#### _A WebSocket base console utility_

A native client plus drop-in utility for adding a telnet like experience to an application
using [_http4s_](https://github.com/http4s/http4s).

### How it works

### Native non-scala Dependencies
_s2n_ is needed for SSL
```
$ brew install s2n
```

### Snapshot Dependencies

This project depends on a couple of snapshots at the moment.
* For Ember websockets - https://github.com/http4s/http4s/pull/7261
* For native polling runtime - https://github.com/typelevel/fs2/pull/3240

### F.A.Q.
#### Where did the name _'Whisker'_ come from?
The initial thought for a basic name was _Web-Socket Console_. Abbreviated as _WSC_,
it looks like it could be read as _"Whisk"_. Since it's being build on top of Cats-Effect
and cats have whiskers, I decided to call this project _Whisker_.