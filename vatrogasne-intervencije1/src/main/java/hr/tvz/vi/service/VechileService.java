/*
 * VechileService VechileService.java.
 *
 */
package hr.tvz.vi.service;

import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.ServiceRepository;
import hr.tvz.vi.orm.Vechile;
import hr.tvz.vi.orm.VechileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class VechileService.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 7:22:17 PM Aug 14, 2021
 */
@Service
public class VechileService extends AbstractService<Vechile> {

  /** The service repository. */
  @Autowired
  ServiceRepository serviceRepository;

  /**
   * Delete vechile.
   *
   * @param vechile the vechile
   */
  public void deleteVechile(Vechile vechile) {
    if (vechile == null) {
      return;
    }
    repository.delete(vechile);
  }

  /**
   * Gets the by id.
   *
   * @param vechileId the vechile id
   * @return the by id
   */
  public Optional<Vechile> getById(Long vechileId) {
    if (vechileId == null) {
      return Optional.empty();
    }
    return repository.findById(vechileId);
  }

  /**
   * Gets the by organization.
   *
   * @param organizationId the organization id
   * @return the by organization
   */
  public List<Vechile> getByOrganization(Long organizationId) {
    if (organizationId == null) {
      return new ArrayList<>();
    }
    return ((VechileRepository) repository).findByOrganizationId(organizationId);
  }

  /**
   * Save or update service record.
   *
   * @param service the service
   * @param vechile the vechile
   */
  public void saveOrUpdateServiceRecord(hr.tvz.vi.orm.Service service, Vechile vechile) {
    if (service == null || vechile == null) {
      return;
    }
    service.setServiceVechile(vechile);
    serviceRepository.save(service);
  }

  /**
   * Save or update vechile.
   *
   * @param vechile the vechile
   * @return the vechile
   */
  public Vechile saveOrUpdateVechile(Vechile vechile) {
    if (vechile == null) {
      return null;
    }
    return repository.save(vechile);
  }

  /**
   * Transfer vechile.
   *
   * @param vechile the vechile
   * @param destination the destination
   */
  public void transferVechile(Vechile vechile, Organization destination) {
    if (vechile == null || destination == null) {
      return;
    }
    vechile.setOrganization(destination);
    repository.save(vechile);
  }

}
