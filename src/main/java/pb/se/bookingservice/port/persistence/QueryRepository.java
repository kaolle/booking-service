package pb.se.bookingservice.port.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pb.se.bookingservice.application.MemberNotFoundException;
import pb.se.bookingservice.domain.FamilyMember;

@Repository
public class QueryRepository {
    @Autowired
    MongoOperations mongoOperations;
    public FamilyMember findFamilyMemberByPhrase(String familyPhrase) {

        FamilyMember familyMember = mongoOperations.findOne(Query.query(Criteria.where("aBitMore").is(familyPhrase)), FamilyMember.class);
        if ( familyMember == null ) {
            throw new MemberNotFoundException("member does not exist");
        }
        return familyMember;
    }
}
