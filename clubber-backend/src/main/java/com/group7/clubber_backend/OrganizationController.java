package com.group7.clubber_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.clubber_backend.Managers.OrganizationManager;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Schemas.Organization.OrganizationGetResponse;
import com.group7.lib.types.Organization.Organization;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    private final OrganizationManager organizationManager;

    public OrganizationController() {
        this.organizationManager = OrganizationManager.getInstance();
    }

    @GetMapping("/{id}")
    public GetResponse getOrganization(@PathVariable String id) {
        Organization organization = this.organizationManager.get(new OrganizationId(id));
        return new OrganizationGetResponse(organization);
    }

    @PostMapping("/")
    public PostResponse createOrganization(@RequestBody PostRequest request) {
        // Organization organization = this.organizationManager.create(request);
        // return new PostResponse(organization);
    }
}
