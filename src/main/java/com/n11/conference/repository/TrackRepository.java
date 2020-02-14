package com.n11.conference.repository;

import com.n11.conference.model.Track;
import org.springframework.data.repository.CrudRepository;

public interface TrackRepository extends CrudRepository<Track, Long> {
}
