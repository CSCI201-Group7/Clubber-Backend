package com.group7.lib.types.Schemas.Announcements;

// Assuming token, orgId, title, content are primary fields for request
// Attachments will be handled as MultipartFile[] in the controller directly
public record PostRequest(
    String organizationId, // ID of the organization posting the announcement
    String title,
    String content
    // String token, // Token will be a header parameter in the controller
) {} 