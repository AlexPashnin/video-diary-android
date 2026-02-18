# Video Diary Application Requirements

---

## Application Type & Platforms

### Primary Platform
- **Native Mobile Apps** (iOS & Android)
  - Kotlin Multiplatform for shared business logic
  - Native UI (SwiftUI for iOS, Jetpack Compose for Android)
  - Camera integration for direct recording
  - Background upload support
  - Push notifications

### Secondary Platform  
- **Web Application**
  - Kotlin/JS with React
  - Full feature parity with mobile
  - Desktop-friendly interface
  - File upload from computer

---

## User Personas

### Primary Persona: "Daily Documenter"
**Sarah, 28, Marketing Manager**
- Records 2-3 minute clips of daily highlights
- Wants to remember year in review
- Shares compilations with family
- Uses iPhone, shoots in 1080p
- Values simplicity and aesthetics

### Secondary Persona: "Life Logger"  
**Marcus, 35, Tech Enthusiast**
- Records everything in 4K
- Creates monthly compilations
- Premium subscriber
- Wants highest quality output
- Power user, explores all features

### Tertiary Persona: "Casual Chronicler"
**Emma, 22, College Student**
- Records sporadically (3-4x per week)
- Mobile-only user
- Limited storage/data
- Shares on social media
- Free tier user

---

## User Stories

### Epic 1: Video Upload & Processing

**US-1.1: Upload Video**
```
As a user
I want to upload a video for a specific day
So that I can capture my daily moments

Acceptance Criteria:
- Can select date (today or past date)
- Maximum file size: 200 MB (Free), 500 MB (Premium), 1 GB (Enterprise)
- Supported formats: MP4, MOV, AVI, MKV, WebM
- Shows upload progress bar
- Can cancel upload mid-way
- Receive notification when processing complete
```

**US-1.2: Replace Existing Video**
```
As a user
I want to replace a video I've already uploaded
So that I can correct mistakes or upload better footage

Acceptance Criteria:
- Can re-upload for same date anytime
- System warns if clip already selected
- Previous clip is deleted if replaced
- Clear warning about affected compilations
- Must confirm replacement with checkbox
```

**US-1.3: View Processing Status**
```
As a user
I want to see my video processing status
So that I know when I can select a clip

Acceptance Criteria:
- Status indicators: Uploading, Processing, Ready, Failed
- Estimated time remaining shown
- Error messages are clear and actionable
- Can navigate away and return
- Push notification when ready
```

### Epic 2: Clip Selection

**US-2.1: Browse Video Timeline**
```
As a user
I want to scrub through my uploaded video with visual previews
So that I can find the perfect moment

Acceptance Criteria:
- Interactive timeline with thumbnail sprite sheet
- Audio waveform visualization
- Smooth video playback
- Frame-by-frame navigation (keyboard arrows)
- Hover shows thumbnail preview
- Current time and duration displayed
```

**US-2.2: Select 1-Second Clip**
```
As a user
I want to select exactly 1 second from my video
So that I can save my favorite moment

Acceptance Criteria:
- Click anywhere on timeline to select
- Visual indicator shows selected 1-second range
- Preview loops selected second before confirming
- Can adjust selection before confirming
- "Select This Second" button
- Confirmation step prevents accidents
```

**US-2.3: View Selected Clips**
```
As a user
I want to see all my previously selected clips in a calendar
So that I can review my daily moments

Acceptance Criteria:
- Monthly calendar view
- Thumbnails shown on days with clips
- Visual status: Has clip (green), Has video/no clip (yellow), Empty (gray)
- Click day to view clip
- Shows date and any notes
- Quick access to create compilation
```

### Epic 3: Compilation Creation

**US-3.1: Create Date Range Compilation**
```
As a user
I want to create a video compilation for any date range
So that I can see my life story over time

Acceptance Criteria:
- Select custom start/end date
- Quick presets: This Week, This Month, Last Month, This Year, All Time
- Shows preview: "X clips found, video will be Y seconds long"
- Lists missing dates (days without clips)
- Can proceed with any number of clips (even if gaps exist)
```

**US-3.2: Choose Watermark Style**
```
As a user
I want to choose where the date appears on each clip
So that the watermark doesn't cover important content

Acceptance Criteria:
- 6 position options: Top-Left, Top-Right, Bottom-Left, Bottom-Right, Center-Top, Center-Bottom
- Visual preview of each position
- Default: Bottom-Right
- Watermark shows date format: "Feb 12, 2026"
- White text with black border (readable on any background)
```

**US-3.3: Select Output Quality**
```
As a user
I want to choose the output quality of my compilation
So that I can optimize for my use case (sharing vs archival)

Acceptance Criteria:
- Quality options: 4K, 1080p, 720p, 480p
- Shows estimated file size for each
- Smart default based on clip resolutions
- Free tier: max 1080p, Premium: full 4K
- Clear explanation of each quality level
```

**US-3.4: Monitor Compilation Progress**
```
As a user
I want to see my compilation being created
So that I know when it's ready to download

Acceptance Criteria:
- Real-time progress: "Processing clip 15 of 30 (50%)"
- Estimated time remaining
- Can navigate away (processing continues)
- Push notification when complete
- Status: Pending, Processing, Completed, Failed
```

**US-3.5: View and Download Compilation**
```
As a user
I want to watch and download my completed compilation
So that I can share it or keep it

Acceptance Criteria:
- In-app video player
- Download button with file size shown
- Shows expiry date (7 days from creation)
- Viewing extends expiry by 7 days
- Can create new compilation with different settings
- Can delete manually before expiry
```

### Epic 4: Account & Quota Management

**US-4.1: View Storage Usage**
```
As a user
I want to see my current usage and limits
So that I know my quota status

Acceptance Criteria:
- Shows: Total clips, uploads today, compilations this month
- Visual progress bars for each quota
- Warnings at 80% and 95% usage
- Clear upgrade prompts for free users
- Monthly compilation counter resets on 1st
```

**US-4.2: Manage Clips**
```
As a user
I want to delete old clips I no longer need
So that I can free up quota (free tier) or clean up

Acceptance Criteria:
- List view and calendar view of all clips
- Bulk selection for deletion
- Warning if clip used in active compilations
- Confirmation before deletion
- Can filter by date range
```

**US-4.3: Upgrade Account**
```
As a user
I want to upgrade to Premium for more features
So that I can enjoy unlimited clips and better quality

Acceptance Criteria:
- Clear comparison table: Free vs Premium vs Enterprise
- Highlights: Storage limits, quality options, processing priority
- One-click upgrade flow
- Immediate access after payment
- Billing management page
```

### Epic 5: Mobile-Specific Features

**US-5.1: Record Video Directly**
```
As a mobile user
I want to record video within the app
So that I don't need to switch to camera app

Acceptance Criteria:
- In-app camera interface
- Switch front/back camera
- Flash toggle
- Timer/countdown option
- Direct upload after recording
- Save to phone's photo library (optional)
```

**US-5.2: Background Upload**
```
As a mobile user
I want uploads to continue when I switch apps
So that I don't have to wait with app open

Acceptance Criteria:
- Upload continues in background
- Progress notification shown
- Completion notification
- Retry on network failure
- Resume partial uploads
```

**US-5.3: Offline Queue**
```
As a mobile user
I want to queue uploads when offline
So that they upload automatically when I have connection

Acceptance Criteria:
- Can select videos while offline
- Shows "Queued for upload"
- Auto-uploads when WiFi available
- Option: Upload on cellular or WiFi-only
- Clear queue status
```

---

## Functional Requirements

### FR-1: Video Upload & Processing

**FR-1.1 Upload Validation**
- Accept formats: MP4, MOV, AVI, MKV, WebM
- File size limits by tier:
  - Free: 200 MB max
  - Premium: 500 MB max
  - Enterprise: 1 GB max
- Duration limits by tier:
  - Free: 5 minutes max
  - Premium: 10 minutes max
  - Enterprise: 30 minutes max
- One video per day per user
- Client-side validation (immediate feedback)
- Server-side validation (security)

**FR-1.2 Smart Transcoding**
- Detect video codec and container
- If H.264 MP4 with bitrate ≤15 Mbps: Pass-through (no transcode)
- Otherwise: Transcode to H.264 MP4
- Support up to 4K resolution (preserve up to 2160p)
- Downscale 8K+ to 4K
- Preserve aspect ratio (portrait/landscape/square)
- Apply "fast start" flag for streaming

**FR-1.3 Timeline Generation**
- Extract thumbnail every 1 second (videos <5 min)
- Create sprite sheet: 10 columns × N rows
- Individual thumbnail: 160×90 pixels
- Sprite format: JPEG, quality 80%, ~150-300 KB
- Generate audio waveform (JSON or PNG)
- Store temporarily (deleted after clip selection)

**FR-1.4 Direct S3 Upload**
- Generate presigned upload URL (expires in 30 min)
- Frontend uploads directly to S3
- No backend proxying of video data
- Backend confirms upload completion
- Verify file existence and size

### FR-2: Clip Selection & Storage

**FR-2.1 Timeline Interface**
- HTML5 video player with seek capability
- Display sprite sheet as timeline background
- Show waveform overlay
- Hover preview: Show thumbnail at position
- Frame-by-frame navigation (keyboard shortcuts)
- 1-second selection window indicator

**FR-2.2 Clip Extraction**
- User selects start time (e.g., 15.5 seconds)
- Extract exactly 1 second (15.5s to 16.5s)
- Use FFmpeg with frame-accurate seeking
- Output: H.264 MP4, preserve source resolution
- Average clip size: 1 MB (1080p), 2-3 MB (4K)
- Store permanently: `/clips/{userId}/{date}.mp4`

**FR-2.3 Video Cleanup**
- Delete original video immediately after clip extraction
- Delete sprite sheet and waveform
- Update video status: CLIP_EXTRACTED
- Publish ClipExtractionCompleted event

**FR-2.4 Clip Replacement**
- User can re-upload video anytime
- System deletes previous clip if exists
- Warns about affected compilations
- Requires user confirmation
- Tracks replacement history

### FR-3: Compilation Creation

**FR-3.1 Date Range Selection**
- Custom start/end date selection
- Quick presets (This Week, This Month, etc.)
- Query available clips in range
- Show count and missing dates
- No minimum clip requirement (1+ clips = valid)

**FR-3.2 Watermark Application**
- Add date text to each clip: "MMM dd, yyyy" format
- 6 position options (corners, top/bottom center)
- Font: DejaVu Sans Bold, size 24
- White text with black border (2px)
- Applied during compilation, not stored on clips

**FR-3.3 Multi-Resolution Handling**
- Determine target resolution from user selection
- Upscale/downscale clips to match target
- Apply watermarks at target resolution
- Concatenate using FFmpeg
- Ensure frame size consistency

**FR-3.4 Quality Options**
- 480p: 854×480, 1.5 Mbps, ~6 MB per 30s
- 720p: 1280×720, 3 Mbps, ~12 MB per 30s
- 1080p: 1920×1080, 5 Mbps, ~20 MB per 30s (default)
- 4K: 3840×2160, 15 Mbps, ~60 MB per 30s (Premium only)

**FR-3.5 Compilation Lifecycle**
- Status: PENDING → PROCESSING → COMPLETED
- Store temporarily: `/compilations/{userId}/{compilationId}.mp4`
- Initial expiry: 7 days from creation
- Extend expiry on view: +7 days
- Maximum lifetime: 30 days
- Auto-delete expired compilations (hourly job)

**FR-3.6 Progress Tracking**
- Publish progress events: "Processing clip X of Y"
- Update percentage (0-100%)
- Estimate time remaining
- WebSocket or polling for real-time updates

### FR-4: Quota & Rate Limiting

**FR-4.1 Storage Quotas by Tier**

Free Tier:
- Max clips: 365 (1 year)
- Max uploads per day: 5
- Max compilations per month: 10
- Max video size: 200 MB
- Max video duration: 5 minutes
- Max compilation quality: 1080p

Premium Tier ($4.99/month):
- Max clips: Unlimited
- Max uploads per day: 20
- Max compilations per month: 50
- Max video size: 500 MB
- Max video duration: 10 minutes
- Max compilation quality: 4K

Enterprise Tier ($19.99/month):
- Max clips: Unlimited
- Max uploads per day: 100
- Max compilations per month: 200
- Max video size: 1 GB
- Max video duration: 30 minutes
- Max compilation quality: 4K
- Processing priority: HIGH

**FR-4.2 Quota Enforcement**
- Check quota before upload (pre-flight)
- Validate file size client-side and server-side
- Track daily upload count (reset at midnight UTC)
- Track monthly compilation count (reset 1st of month)
- Increment clip count on successful extraction
- Decrement clip count on deletion

**FR-4.3 Quota Notifications**
- Warning at 80% clip quota (free tier only)
- Critical warning at 95% clip quota
- Block upload at 100% with upgrade prompt
- Email notification when limits approached
- In-app quota dashboard

### FR-5: User Management

**FR-5.1 Authentication**
- Email + password registration
- JWT access tokens (15 min expiry)
- Refresh tokens (7 day expiry)
- Password requirements: Min 8 chars, 1 uppercase, 1 number
- Email verification required
- Password reset flow

**FR-5.2 User Tiers**
- Default: FREE on registration
- Upgrade via payment integration (Stripe)
- Immediate tier change on successful payment
- Downgrade: Keep data, enforce lower limits
- Grace period: 7 days after subscription ends

**FR-5.3 User Profile**
- Display name
- Email (verified)
- Profile picture (optional)
- Timezone (for date calculations)
- Preferences: Default watermark position, notification settings

---

## Non-Functional Requirements

### NFR-1: Performance

**NFR-1.1 Upload Speed**
- Direct S3 upload (no backend bottleneck)
- Support resumable uploads for files >100 MB
- Target: 200 MB video uploads in <2 minutes on 50 Mbps connection

**NFR-1.2 Processing Time**
- Pass-through (H.264 MP4): Timeline ready in 10-15 seconds
- Transcode required: Timeline ready in 45-90 seconds
- Clip extraction: Complete within 5 seconds
- Compilation (30 clips, 1080p): Complete within 60-90 seconds
- Compilation (30 clips, 4K): Complete within 2-3 minutes

**NFR-1.3 API Response Time**
- p95 latency <500ms for all read operations
- p99 latency <1000ms
- Upload presigned URL generation: <200ms

**NFR-1.4 Scalability**
- Support 10,000 concurrent users
- Handle 1,000 video uploads per hour
- Auto-scale workers based on queue depth
- Target: 100,000 users in first year

### NFR-2: Reliability

**NFR-2.1 Availability**
- 99.5% uptime SLA (excluding planned maintenance)
- Scheduled maintenance: 1st Sunday of month, 2-4 AM UTC
- Max 4 hours downtime per month

**NFR-2.2 Data Durability**
- Clips stored in S3 Standard (99.999999999% durability)
- Database backups: Daily, retained for 30 days
- Point-in-time recovery: 7 days

**NFR-2.3 Error Handling**
- All processing failures logged
- Failed jobs retry 3 times with exponential backoff
- Dead letter queue for manual intervention
- User notified of permanent failures

### NFR-3: Security

**NFR-3.1 Data Protection**
- All data encrypted in transit (TLS 1.3)
- S3 server-side encryption at rest (AES-256)
- Database encryption at rest
- Presigned URLs expire in 30 minutes
- JWT tokens signed with RS256

**NFR-3.2 Access Control**
- Users can only access their own videos/clips/compilations
- Resource ownership validation on every request
- Rate limiting: 100 requests per minute per user
- DDoS protection via CloudFlare

**NFR-3.3 Privacy**
- All videos/clips private by default
- No public access without explicit sharing (future feature)
- GDPR compliance: Right to delete all data
- Data retention: Compilations auto-delete, clips kept until user deletes

### NFR-4: Usability

**NFR-4.1 Mobile First**
- Responsive design: Works on 320px+ screens
- Touch-optimized controls
- Native mobile apps for iOS and Android
- Offline queue for uploads

**NFR-4.2 Accessibility**
- WCAG 2.1 Level AA compliance
- Keyboard navigation for all features
- Screen reader compatible
- Color contrast ratios meet standards

**NFR-4.3 Browser Support**
- Chrome 90+
- Safari 14+
- Firefox 88+
- Edge 90+
- Mobile browsers: iOS Safari 14+, Chrome Mobile 90+

### NFR-5: Monitoring & Observability

**NFR-5.1 Logging**
- Centralized logging (ELK stack)
- Structured JSON logs
- Log retention: 30 days
- Log levels: DEBUG (workers), INFO (services)

**NFR-5.2 Metrics**
- Prometheus + Grafana dashboards
- Key metrics: Upload success rate, processing time, queue depth, error rate
- Alerts: Queue depth >100, error rate >5%, disk usage >80%

**NFR-5.3 Tracing**
- Distributed tracing (Jaeger)
- Trace all user requests across services
- Performance profiling for slow operations

---

## Technical Architecture Summary

### Microservices

1. **API Gateway** (Spring Cloud Gateway)
   - Request routing
   - JWT validation
   - Rate limiting
   - CORS handling

2. **Auth Service** (Port 8081)
   - User registration/login
   - JWT token generation
   - Password management

3. **Video Service** (Port 8082)
   - Video upload coordination
   - Presigned URL generation
   - Video metadata management
   - Quota validation

4. **Clip Service** (Port 8083)
   - Clip selection handling
   - Clip metadata storage
   - Calendar view data

5. **Compilation Service** (Port 8084)
   - Compilation requests
   - Progress tracking
   - Download URL generation

6. **Storage Service** (Port 8085)
   - S3 operations wrapper
   - Presigned URL generation
   - File management

7. **Notification Service** (Port 8086)
   - Email notifications
   - Push notifications (mobile)
   - In-app notifications

### Background Workers

1. **Video Processing Worker**
   - Transcode videos (if needed)
   - Generate sprite sheets
   - Generate waveforms
   - Publish processing complete events

2. **Clip Extraction Worker**
   - Extract 1-second clips
   - Upload to S3
   - Delete original video
   - Publish clip ready events

3. **Compilation Worker**
   - Download clips
   - Apply watermarks
   - Concatenate clips
   - Upload compilation
   - Publish completion events

### Data Stores

1. **PostgreSQL Databases**
   - auth-db: Users, tokens
   - video-db: Video entries metadata
   - clip-db: Selected clips metadata
   - compilation-db: Compilations metadata

2. **S3 Buckets**
   - videodiary-temp: Original videos during processing
   - videodiary-clips: Permanent 1-second clips
   - videodiary-compilations: Temporary compilations

3. **Kafka Topics**
   - video.events: Video lifecycle events
   - clip.events: Clip lifecycle events
   - compilation.events: Compilation lifecycle events
   - notification.events: Notification requests

4. **Redis**
   - JWT token blacklist
   - Rate limiting counters
   - Session cache

---

## Data Flow Diagrams

### Upload to Clip Flow

```
1. User uploads video (Feb 12, 2026)
   ↓
2. Frontend requests presigned URL from Video Service
   ↓
3. Video Service validates quota, generates URL
   ↓
4. Frontend uploads directly to S3
   ↓
5. Frontend confirms upload to Video Service
   ↓
6. Video Service publishes VideoUploaded event to Kafka
   ↓
7. Video Processing Worker consumes event
   ↓
8. Worker transcodes (if needed) + generates sprite/waveform
   ↓
9. Worker publishes VideoProcessed event
   ↓
10. Frontend notified: "Timeline ready"
   ↓
11. User scrubs timeline, selects 1 second (at 15.5s)
   ↓
12. Clip Service publishes ClipSelected event
   ↓
13. Clip Worker extracts 1-second clip, uploads to S3
   ↓
14. Clip Worker deletes original video + sprite
   ↓
15. Clip Worker publishes ClipReady event
   ↓
16. Frontend notified: "Clip saved"
   ↓
Result: 1 MB clip stored permanently, original deleted
```

### Compilation Flow

```
1. User requests compilation (Feb 1-28)
   ↓
2. Compilation Service fetches all clips in range from Clip Service
   ↓
3. Validates at least 1 clip exists
   ↓
4. Creates compilation record (PENDING)
   ↓
5. Publishes CompilationRequested event to Kafka
   ↓
6. Compilation Worker consumes event
   ↓
7. Worker updates status to PROCESSING
   ↓
8. For each clip:
   - Download from S3
   - Apply date watermark at target resolution
   - Save to temp directory
   ↓
9. Worker concatenates all watermarked clips
   ↓
10. Worker uploads compilation to S3
   ↓
11. Worker updates status to COMPLETED, sets expiry (7 days)
   ↓
12. Worker publishes CompilationCompleted event
   ↓
13. Notification Service sends email: "Your compilation is ready!"
   ↓
14. User watches/downloads
   ↓
15. After 7 days (or on activity): Compilation expires and auto-deletes
```

---

## API Endpoints Summary

### Authentication
```
POST   /auth/register
POST   /auth/login
POST   /auth/refresh
POST   /auth/logout
GET    /auth/me
```

### Videos
```
POST   /videos/prepare-upload
POST   /videos/{id}/upload-complete
GET    /videos
GET    /videos/{id}
GET    /videos/check-existing?date={date}
DELETE /videos/{id}
```

### Clips
```
POST   /clips/select
GET    /clips
GET    /clips/{id}
GET    /clips?month={month}&year={year}
DELETE /clips/{id}
```

### Compilations
```
POST   /compilations/create
GET    /compilations/{id}
GET    /compilations/{id}/status
GET    /compilations/{id}/stream
GET    /compilations/{id}/download
DELETE /compilations/{id}
GET    /compilations/history
```

### User/Quota
```
GET    /users/quota
GET    /users/usage
POST   /users/upgrade
```

---

## Success Metrics (KPIs)

### User Engagement
- Daily Active Users (DAU)
- Monthly Active Users (MAU)
- Average clips per user per month
- Compilations created per user per month
- Retention rate (Day 1, Day 7, Day 30)

### Technical Performance
- Upload success rate (target: >98%)
- Processing completion rate (target: >99%)
- Average processing time (target: <60s for 1080p)
- API p95 latency (target: <500ms)
- System uptime (target: >99.5%)

### Business Metrics
- Free to Premium conversion rate (target: 5%)
- Monthly Recurring Revenue (MRR)
- Customer Acquisition Cost (CAC)
- Lifetime Value (LTV)
- Churn rate (target: <5% monthly)

---

## Future Enhancements (Post-MVP)

### Phase 2 (3-6 months post-launch)
- Social sharing: Public compilation links
- Music overlay: Royalty-free music library
- Fade transitions between clips
- AI-suggested "best moments" from videos
- Multi-device sync

### Phase 3 (6-12 months post-launch)
- Family/group accounts (shared calendars)
- Collaboration features
- Export to social media (Instagram, TikTok format)
- Advanced editing (trim clips, add text)
- Templates (birthday countdowns, travel logs)

### Phase 4 (12+ months post-launch)
- AI-generated compilations (themed: "Happy moments", "Outdoor adventures")
- Voice-over support
- Live Photo / GIF export
- Physical products (printed photo books, USB drives)
- API for third-party integrations

---

This document serves as the complete requirements specification for the Video Diary application MVP. All architectural decisions, user stories, and technical requirements are captured here for development planning.
