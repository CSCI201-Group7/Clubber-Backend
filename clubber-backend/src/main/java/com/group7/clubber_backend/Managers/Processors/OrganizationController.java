package com.group7.clubber_backend;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.clubber_backend.Managers.OrganizationManager;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Schemas.Organizations.DeleteResponse;
import com.group7.lib.types.Schemas.Organizations.GetResponse;
import com.group7.lib.types.Schemas.Organizations.PostRequest;
import com.group7.lib.types.Schemas.Organizations.PostResponse;
import com.group7.lib.types.Schemas.Organizations.PutRequest;
import com.group7.lib.types.Organization.Organization;
import com.group7.lib.types.Organization.Category;
import com.group7.lib.types.Organization.Visibility;
import java.util.List;
import java.util.Date;

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
        return new GetResponse(organization);
    }

    @PostMapping("/")
    public PostResponse createOrganization(@RequestBody PostRequest request) {
        Organization organization = new Organization(
            new OrganizationId(java.util.UUID.randomUUID().toString()),
            request.name(),
            Category.valueOf(request.category()),
            request.tags(),
            new com.group7.lib.types.Ids.UserId[0],
            new com.group7.lib.types.Ids.UserId[0],
            0.0,
            new com.group7.lib.types.Ids.ReviewId[0],
            new com.group7.lib.types.Organization.RecruitmentInfo(
                request.recruitmentInfo().openStatus(),
                request.recruitmentInfo().applicationLink(),
                request.recruitmentInfo().deadline(),
                request.recruitmentInfo().gradeRequirements(),
                request.recruitmentInfo().majorRequirements()
            ),
            new com.group7.lib.types.Organization.OrganizationInfo(
                request.info().numberOfMembers(),
                request.info().yearOfEstablishment(),
                request.info().description(),
                request.info().meetingSchedule(),
                request.info().location(),
                request.info().contactEmail(),
                request.info().socialMediaLinks()
            ),
            new com.group7.lib.types.Schemas.Organizations.Event[0],
            new com.group7.lib.types.Schemas.Organizations.Announcement[0],
            new com.group7.lib.types.Ids.ImageId(java.util.UUID.randomUUID().toString()),
            new com.group7.lib.types.Ids.ImageId(java.util.UUID.randomUUID().toString()),
            Visibility.valueOf(request.visibility())
        );
        this.organizationManager.create(organization);
        return new PostResponse(organization);
    }

    @PutMapping("/{id}")
    public GetResponse updateOrganization(@PathVariable String id, @RequestBody PutRequest request) {
        Organization organization = this.organizationManager.get(new OrganizationId(id));
        organization.setName(request.name());
        organization.setCategory(Category.valueOf(request.category()));
        organization.setTags(request.tags());
        organization.setRecruitmentInfo(new com.group7.lib.types.Organization.RecruitmentInfo(
            request.recruitmentInfo().openStatus(),
            request.recruitmentInfo().applicationLink(),
            request.recruitmentInfo().deadline(),
            request.recruitmentInfo().gradeRequirements(),
            request.recruitmentInfo().majorRequirements()
        ));
        organization.setInfo(new com.group7.lib.types.Organization.OrganizationInfo(
            request.info().numberOfMembers(),
            request.info().yearOfEstablishment(),
            request.info().description(),
            request.info().meetingSchedule(),
            request.info().location(),
            request.info().contactEmail(),
            request.info().socialMediaLinks()
        ));
        organization.setVisibility(Visibility.valueOf(request.visibility()));
        this.organizationManager.update(organization);
        return new GetResponse(organization);
    }

    @DeleteMapping("/{id}")
    public DeleteResponse deleteOrganization(@PathVariable String id) {
        try {
            this.organizationManager.delete(new OrganizationId(id));
            return new DeleteResponse(true, "Organization deleted successfully");
        } catch (Exception e) {
            return new DeleteResponse(false, "Failed to delete organization: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/reviews")
    public List<com.group7.lib.types.Review.Review> getOrganizationReviews(@PathVariable String id) {
        return this.organizationManager.getOrganizationReviews(new OrganizationId(id));
    }

    @GetMapping("/{id}/events")
    public List<com.group7.lib.types.Schemas.Organizations.Event> getOrganizationEvents(@PathVariable String id) {
        return this.organizationManager.getOrganizationEvents(new OrganizationId(id));
    }

    @PostMapping("/{id}/events")
    public com.group7.lib.types.Schemas.Organizations.Event createOrganizationEvent(
            @PathVariable String id, 
            @RequestBody com.group7.lib.types.Schemas.Organizations.Event event) {
        return this.organizationManager.createOrganizationEvent(new OrganizationId(id), event);
    }

    @GetMapping("/{id}/announcements")
    public List<com.group7.lib.types.Schemas.Organizations.Announcement> getOrganizationAnnouncements(@PathVariable String id) {
        return this.organizationManager.getOrganizationAnnouncements(new OrganizationId(id));
    }

    @PostMapping("/{id}/announcements")
    public com.group7.lib.types.Schemas.Organizations.Announcement createOrganizationAnnouncement(
            @PathVariable String id, 
            @RequestBody com.group7.lib.types.Schemas.Organizations.Announcement announcement) {
        return this.organizationManager.createOrganizationAnnouncement(new OrganizationId(id), announcement);
    }

    @GetMapping("/{id}/analytics")
    public com.group7.lib.types.Organization.Analytics getOrganizationAnalytics(@PathVariable String id) {
        return this.organizationManager.getOrganizationAnalytics(new OrganizationId(id));
    }
} 