package com.chatop.rental_backend.presenter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.chatop.rental_backend.dto.RentalDto;
import com.chatop.rental_backend.dto.RentalListDto;
import com.chatop.rental_backend.model.Rental;

@Component
public class RentalPresenter implements Presenter<RentalDto, Rental> {

  @Override
  public RentalDto present(final Rental model) {
    return new RentalDto(model.getId(), model.getName(), model.getSurface(), model.getPrice(),
        model.getPictures().stream().map(this::storagePathToPublic).toList(),
        model.getDescription(), model.getOwner().getId(), Presenter.format(model.getCreatedAt()),
        Presenter.format(model.getUpdatedAt()));
  }

  public RentalListDto presentModelList(final Iterable<Rental> rentals) {
    final List<Rental> output = new ArrayList<>();
    rentals.iterator().forEachRemaining(output::add);

    return new RentalListDto(output.stream()
        .map(model -> new RentalListDto.RentalDto(model.getId(), model.getName(),
            model.getSurface(), model.getPrice(),
            model.getPictures().stream().map(this::storagePathToPublic).toList().getFirst(),
            model.getDescription(), model.getOwner().getId(),
            Presenter.format(model.getCreatedAt()), Presenter.format(model.getUpdatedAt())))
        .toList());
  }

  private String storagePathToPublic(final String filePath) {
    final var storagePath = Path.of(filePath);
    final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

    // These are files stored by our FileStorageHelper
    if (storagePath.startsWith("storage")) {
      return baseUrl + '/' + storagePath.subpath(0, 1).relativize(storagePath);
    }

    return filePath;
  }

}
