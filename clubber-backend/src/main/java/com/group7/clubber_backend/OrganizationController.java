package com.group7.clubber_backend;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.group7.clubber_backend.Managers.OrganizationManager;
import com.group7.clubber_backend.Managers.UserManager;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Organization.Organization;
import com.group7.lib.types.Schemas.Organizations.GetAllResponse;
import com.group7.lib.types.Schemas.Organizations.GetResponse;
import com.group7.lib.types.Schemas.Organizations.PostRequest;
import com.group7.lib.types.Schemas.Organizations.PostResponse;
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

    @GetMapping
    public GetAllResponse getAll() {
        List<Organization> organizations = OrganizationManager.getInstance().getAll();
        return new GetAllResponse(organizations);
    }

    @PostMapping
    public PostResponse create(@RequestHeader("Authorization") String token, @RequestBody PostRequest request) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        User user = UserManager.getInstance().get(new UserId(token));
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        // Create lists for member and admin IDs
        List<UserId> memberIds = new ArrayList<>();
        memberIds.add(user.getId());

        List<UserId> adminIds = new ArrayList<>();
        adminIds.add(user.getId());

        OrganizationId organizationId = (OrganizationId) OrganizationManager.getInstance().create(
                new Organization(
                        null, // id will be set by the database
                        request.name(),
                        request.type(),
                        request.description(),
                        request.contactEmail(),
                        request.recruitingStatus(),
                        request.location(),
                        request.links(),
                        memberIds,
                        adminIds,
                        new ArrayList<>(), // empty list for reviewIds
                        null, // profileImageId
                        new ArrayList<>(), // empty list for eventIds
                        new ArrayList<>(), // empty list for announcementIds
                        null // bannerImageId
                )
        );

        if (organizationId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create organization");
        }

        return new PostResponse(organizationId);
    }
}
