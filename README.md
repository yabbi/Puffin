# Puffin

**Puffin** is a minimalist, offline, open-source audiobook player for Android — built for reliability and simplicity. No accounts, no ads, no tracking; just your own audiobooks, played beautifully.

Open source audiobook player with the best experience.

## Screenshots

<p>
  <img src="UI%20shots/Playstore.png" width="180" alt="Puffin library screen" />
  <img src="UI%20shots/Playstore-1.png" width="180" alt="Puffin book details screen" />
  <img src="UI%20shots/Playstore-2.png" width="180" alt="Puffin now playing screen" />
  <img src="UI%20shots/Playstore-3.png" width="180" alt="Puffin history screen" />
  <img src="UI%20shots/Playstore-4.png" width="180" alt="Puffin settings screen" />
  <img src="UI%20shots/Playstore-5.png" width="180" alt="Puffin onboarding screen" />
  <img src="UI%20shots/Playstore-6.png" width="180" alt="Puffin audiobook controls" />
</p>

## Documentation

- [Privacy Policy](docs/privacy-policy.md)
- [Terms of Use](docs/terms.md)
- [Open Source Licenses](docs/licenses.md)
- [FAQ](docs/faq.md)
- [Architecture](docs/architecture.md) · [Development](docs/development.md)

## Building

```
./gradlew :app:assemblePlayDebug      # debug APK
./gradlew :app:bundlePlayRelease      # release AAB (requires signing config)
./gradlew voiceUnitTest               # run unit tests
```

See [docs/development.md](docs/development.md) for environment setup.

## License & attribution

Puffin is licensed under the [GNU GPLv3](LICENSE.md). By contributing, you agree to license your code under the same terms.

Puffin is an independent fork of the open-source [Voice](https://github.com/PaulWoitaschek/Voice) audiobook player (© Paul Woitaschek and contributors, GPLv3). It is not affiliated with or endorsed by the Voice project. See [ATTRIBUTION.md](ATTRIBUTION.md).
