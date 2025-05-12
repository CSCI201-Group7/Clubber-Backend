package com.group7.clubber_backend;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.group7.clubber_backend.Managers.AnnouncementManager;
import com.group7.clubber_backend.Managers.FileManager;
import com.group7.clubber_backend.Managers.OrganizationManager;
import com.group7.clubber_backend.Managers.UserManager;
import com.group7.clubber_backend.Processors.CredentialProcessor;
import com.group7.lib.types.Announcement.Announcement;
import com.group7.lib.types.Announcement.AnnouncementImportance;
import com.group7.lib.types.Ids.AnnouncementId;
import com.group7.lib.types.Ids.FileId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Organization.Organization;
import com.group7.lib.types.Schemas.Announcements.GetResponse;
import com.group7.lib.types.Schemas.Announcements.ListResponse;
import com.group7.lib.types.Schemas.Announcements.PostResponse;
import com.group7.lib.types.User.User;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    private final AnnouncementManager announcementManager = AnnouncementManager.getInstance();
    private final OrganizationManager organizationManager = OrganizationManager.getInstance();
    private final FileManager fileManager = FileManager.getInstance();
    private final CredentialProcessor credentialProcessor = CredentialProcessor.getInstance();
    private final UserManager userManager = UserManager.getInstance();

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public PostResponse createAnnouncement(
            @RequestHeader("Authorization") String token,
            @RequestParam("organizationId") String organizationIdStr,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments,
            @RequestParam(value = "importance", required = false) AnnouncementImportance importance) {

        UserId authorId = credentialProcessor.verifyToken(token);
        if (authorId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        User user = userManager.get(authorId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        OrganizationId organizationId = new OrganizationId(organizationIdStr);
        Organization organization = organizationManager.get(organizationId);
        if (organization == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found");
        }

        // Check if the user is an admin of the organization
        if (organization.adminIds() == null || !organization.adminIds().contains(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an admin of this organization");
        }

        List<FileId> attachmentIds = new ArrayList<>();
        if (attachments != null) {
            for (MultipartFile attachment : attachments) {
                if (attachment != null && !attachment.isEmpty()) {
                    try {
                        String originalFilename = attachment.getOriginalFilename();
                        String extension = "";
                        if (originalFilename != null && originalFilename.contains(".")) {
                            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }
                        String filename = UUID.randomUUID().toString() + extension;
                        FileId uploadedFileId = fileManager.upload(filename, attachment.getInputStream(), attachment.getContentType());
                        if (uploadedFileId != null) {
                            attachmentIds.add(uploadedFileId);
                        }
                    } catch (IOException e) {
                        // Log error and continue, or throw exception
                        System.err.println("Failed to upload attachment: " + e.getMessage());
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload an attachment");
                    }
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();
        Announcement newAnnouncement = new Announcement(
                new AnnouncementId(null), // ID will be set by the database
                organizationId,
                authorId,
                title,
                content,
                attachmentIds,
                now,
                now,
                importance != null ? importance : AnnouncementImportance.Normal
        );

        AnnouncementId newAnnouncementId = (AnnouncementId) announcementManager.create(newAnnouncement);
        if (newAnnouncementId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create announcement");
        }

        // Add announcementId to the organization's list of announcementIds
        List<AnnouncementId> currentAnnouncementIds = new ArrayList<>(organization.announcementIds() != null ? organization.announcementIds() : new ArrayList<>());
        currentAnnouncementIds.add(newAnnouncementId);

        Organization updatedOrganization = new Organization(
                organization.id(),
                organization.name(),
                organization.type(),
                organization.description(),
                organization.contactEmail(),
                organization.recruitingStatus(),
                organization.location(),
                organization.links(),
                organization.memberIds(),
                organization.adminIds(),
                organization.reviewIds(),
                organization.profileImageId(),
                organization.eventIds(),
                currentAnnouncementIds // updated list
        );
        organizationManager.update(updatedOrganization);

        return new PostResponse(newAnnouncementId);
    }

    @GetMapping("/{announcementId}")
    public GetResponse getAnnouncementById(@PathVariable String announcementId) {
        Announcement announcement = announcementManager.get(new AnnouncementId(announcementId));
        if (announcement == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }
        return new GetResponse(announcement);
    }

    @GetMapping("/organizations/{organizationId}")
    public ListResponse getAnnouncementsByOrganizationId(@PathVariable String organizationId) {
        OrganizationId orgId = new OrganizationId(organizationId);
        Organization organization = organizationManager.get(orgId);
        if (organization == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found");
        }
        List<AnnouncementId> announcementIds = new ArrayList<>(organization.announcementIds() != null ? organization.announcementIds() : new ArrayList<>());
        if (announcementIds.isEmpty()) {
            return new ListResponse(new ArrayList<>());
        }
        List<Announcement> announcements = announcementManager.list(announcementIds.toArray(AnnouncementId[]::new));
        return ListResponse.fromAnnouncements(announcements);
    }
}
