# ☁️ Photogram Backup

Transform Telegram into your unlimited, organized cloud storage for photos.

## 🌟 What is Photogram?
Photogram is a modern Android app that automatically backs up your photos to Telegram. Each folder on your phone becomes a separate Topic in your private Telegram Group, keeping everything perfectly organized.

## 💡 Unlimited Storage
Telegram offers free, unlimited cloud storage. Photogram leverages this to give you worry-free photo backups.

## ✨ Features
| Feature | Description |
|---------|-------------|
| 🚀 Smart Delta Sync | Uses MediaStore to detect only new photos — fast and battery-friendly |
| 🔄 Background Engine | Powered by WorkManager for reliable syncing even after reboots |
| 📁 Folder Organization | Each phone folder = one Telegram Topic |
| 📊 Dashboard | Real-time stats on uploads, usage limits, and sync status |
| 🎨 Modern UI | Glassmorphism design with smooth animations |
| ☁️ Cloud Memory | History stored in Telegram — reinstall-proof |
| 📱 Android 14 Ready | Full support for latest Android permissions |

## 📱 Screenshots
*Login → Main → Dashboard → Settings*

## 🚀 Quick Start

### 1. Create Telegram Group
1. Create a new Group in Telegram
2. Enable Topics in Group Settings
3. Add your bot to the group as Admin
4. Get the Group Chat ID (e.g., -100123456789)

### 2. Install & Configure
1. Download APK from [Releases](#) or build from source
2. Open Settings in the app
3. Enter Bot Token and Chat ID
4. Select folders to backup
5. Tap "Sync Now"

## 🏗️ Building from Source

### Prerequisites
- Android Studio Hedgehog+
- JDK 17
- Android SDK 34

### Build Steps
```bash
git clone https://github.com/yourusername/photogram-backup.git
cd photogram-backup
./gradlew assembleDebug
```

### Environment Variables
| Variable | Description |
|----------|-------------|
| `TELEGRAM_BOT_TOKEN` | Your Telegram Bot Token |

## 🛠️ Tech Stack
| Component | Technology |
|-----------|------------|
| Language | Java |
| Network | OkHttp 4 |
| Database | SQLite |
| Background | WorkManager |
| UI | XML + Material Design |

## 📊 Architecture
```
app/
├── MainActivity       # Folder selection & sync trigger
├── DashboardActivity  # Stats & usage monitoring
├── SettingsActivity   # Bot configuration
├── BackupService      # Background sync engine
├── TelegramHelper     # Telegram API integration
├── DatabaseHelper     # Local SQLite operations
├── MediaStoreScanner  # Photo detection
└── BackupWorker       # WorkManager worker
```

## 🔐 Security
- No Hardcoded Secrets — Config injected via build system
- Usage Limits — Configurable daily upload limits
- Wi-Fi Only Option — Save mobile data

## 🤖 GitHub Workflow

This project includes GitHub Actions workflow for automated building:

### Workflow Features:
- **Automatic Builds**: On push to main/master branches
- **APK Artifacts**: Debug and release APKs available as artifacts
- **Testing**: Automated test execution
- **Linting**: Code quality checks
- **Release Automation**: Tag-based releases with APK attachments

### Manual Trigger:
You can manually trigger the workflow from the GitHub Actions tab.

### Secrets Configuration (for signing):
Add these secrets to your GitHub repository for signed release builds:
- `ANDROID_KEYSTORE_BASE64`: Base64 encoded keystore file
- `ANDROID_KEYSTORE_PASSWORD`: Keystore password
- `ANDROID_KEY_ALIAS`: Key alias
- `ANDROID_KEY_PASSWORD`: Key password

## 📄 License
MIT License - see LICENSE file for details

## 🤝 Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📞 Support
For issues and feature requests, please use the [GitHub Issues](https://github.com/yourusername/photogram-backup/issues) page.