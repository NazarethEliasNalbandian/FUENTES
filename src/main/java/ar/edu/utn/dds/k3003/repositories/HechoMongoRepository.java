package ar.edu.utn.dds.k3003.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import ar.edu.utn.dds.k3003.model.HechoMongo;

public interface HechoMongoRepository extends MongoRepository<HechoMongo,String>{

}
