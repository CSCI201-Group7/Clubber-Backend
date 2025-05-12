package com.group7.clubber_backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

import com.group7.clubber_backend.Managers.FileManager;
import com.group7.clubber_backend.Managers.OrganizationManager;
import com.group7.clubber_backend.Managers.UserManager;
import com.group7.clubber_backend.Processors.CredentialProcessor;
import com.group7.lib.types.Ids.FileId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Organization.Organization;
import com.group7.lib.types.Organization.OrganizationLinks;
import com.group7.lib.types.Organization.OrganizationType;
import com.group7.lib.types.Organization.RecruitingStatus;
import com.group7.lib.types.Schemas.ListResponse;
import com.group7.lib.types.Schemas.Organizations.GetResponse;
import com.group7.lib.types.Schemas.PostResponse;
import com.group7.lib.types.User.User;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    @GetMapping("/{id}")
    public GetResponse get(@PathVariable String id) {
        Organization organization = OrganizationManager.getInstance().get(new OrganizationId(id));
        if (organization == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found");
        }
        return new GetResponse(organization);
    }

    // Get all organizations that the user is a member of
    @GetMapping
    public ListResponse<GetResponse> getMembers(
            @RequestParam("userId") String userId
    ) {
        User user = UserManager.getInstance().get(new UserId(userId));
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<Organization> organizations = OrganizationManager.getInstance().search("memberIds:" + userId);
        return ListResponse.fromList(organizations.stream().map(GetResponse::new).collect(Collectors.toList()));
    }

    @GetMapping("/all")
    public ListResponse<GetResponse> getAll() {
        List<Organization> organizations = OrganizationManager.getInstance().getAll();
        return ListResponse.fromList(organizations.stream().map(GetResponse::new).collect(Collectors.toList()));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public PostResponse create(@RequestHeader("Authorization") String token,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("description") String description,
            @RequestParam("contactEmail") String contactEmail,
            @RequestParam("location") String location,
            @RequestParam(value = "links.website", required = false) String linksWebsite,
            @RequestParam(value = "links.instagram", required = false) String linksInstagram,
            @RequestParam(value = "links.discord", required = false) String linksDiscord,
            @RequestParam(value = "links.linkedIn", required = false) String linksLinkedIn,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "logoFilename", required = false) String logoFilename
    ) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        // Verify the token and get the actual UserId
        UserId actualUserId = CredentialProcessor.getInstance().verifyToken(token);
        if (actualUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        User user = UserManager.getInstance().get(actualUserId); // Use the verified UserId
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        // Create lists for member and admin IDs
        List<UserId> memberIds = new ArrayList<>();
        memberIds.add(user.id());

        List<UserId> adminIds = new ArrayList<>();
        adminIds.add(user.id());

        FileId logoId = null;
        if (logo != null && !logo.isEmpty() && logoFilename != null) {
            try {
                // Get the file extension from the filename
                String extension = logoFilename.substring(logoFilename.lastIndexOf("."));
                logoId = FileManager.getInstance().upload(UUID.randomUUID().toString() + "_" + extension, logo.getInputStream(), logo.getContentType());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload profile image");
            }
        }

        // Manually construct OrganizationLinks
        OrganizationLinks links = new OrganizationLinks(
                linksWebsite,
                linksLinkedIn,
                linksInstagram,
                linksDiscord
        );

        // Manually construct OrganizationType from String
        OrganizationType orgType;
        try {
            orgType = OrganizationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid organization type: " + type);
        }

        OrganizationId organizationId = (OrganizationId) OrganizationManager.getInstance().create(
                new Organization(
                        null, // id will be set by the database
                        name,
                        orgType,
                        description,
                        contactEmail,
                        RecruitingStatus.Unknown,
                        location,
                        links,
                        memberIds,
                        adminIds,
                        new ArrayList<>(),
                        logoId == null ? null : logoId.toString(),
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );

        if (organizationId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create organization");
        }

        return new PostResponse(organizationId);
    }
}
