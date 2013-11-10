/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.envers.test.various;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class HsqlTest {
    /*
    Query generated by Hibernate from an old demo, when criteria was used:  (works)

    select
        this_.id as id2_0_,
        this_._revision as column2_2_0_,
        this_._revision_type as column3_2_0_,
        this_.name as name2_0_,
        this_.surname as surname2_0_,
        this_.address_id as address6_2_0_
    from
        Person_versions this_
    where
        this_._revision = (
            select
                max(e2_._revision) as y0_
            from
                Person_versions e2_
            where
                e2_._revision<=1
                and this_.id=e2_.id
        )
        and this_.id=1

    Query generated by Hibernate from a new demo, when query generator is used:
    (throws Column not found: ID in statement because of "person_ver0_.id=person_ver1_.id")    

    select
        person_ver0_.id as id3_,
        person_ver0_._revision as column2_3_,
        person_ver0_._revision_type as column3_3_,
        person_ver0_.name as name3_,
        person_ver0_.surname as surname3_,
        person_ver0_.address_id as address6_3_
    from
        Person_versions person_ver0_
    where
        person_ver0_._revision_type<>2
        and person_ver0_._revision=(
            select
                max(person_ver1_._revision)
            from
                Person_versions person_ver1_
            where
                person_ver1_._revision<=1
                and person_ver0_.id=person_ver1_.id
        )
        and person_ver0_.id=1

        Both queries work from HSQL console
        (to run: java -cp hsqldb.jar org.hsqldb.util.DatabaseManager -user sa -url jdbc:hsqldb:file:/tmp/_versions_demo.db)

        TODO: post hibernate bug
     */

    public static void main(String[] argv) {
        Map<String, String> configurationOverrides = new HashMap<String, String>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConsolePU", configurationOverrides);
        EntityManager entityManager = emf.createEntityManager();

        populateTestData(entityManager);

        entityManager.getTransaction().begin();

        Session sesion = (Session) entityManager.getDelegate();
        System.out.println(sesion.createQuery(
                "select e from org.hibernate.envers.demo.Person_versions e " +
                        "where " +
                        "e.originalId._revision.id =" +
                        "(select max(e2.originalId._revision.id) " +
                        "from org.hibernate.envers.demo.Person_versions e2 " +
                        "where e.originalId.id = :p0) ")
                .setParameter("p0", 1)
                .list());

        entityManager.getTransaction().commit();

        entityManager.close();
        emf.close();
    }

    private static void populateTestData(EntityManager entityManager) {
        entityManager.getTransaction().begin();

        if (!hasData(entityManager)) {
            Person p1 = new Person();

            Address a1 = new Address();

            p1.setName("James");
            p1.setSurname("Bond");
            p1.setAddress(a1);

            a1.setStreetName("MI6");
            a1.setHouseNumber(18);
            a1.setFlatNumber(25);
            a1.setPersons(new HashSet<Person>());
            a1.getPersons().add(p1);

            entityManager.persist(a1);

            entityManager.persist(p1);

            System.out.println("The DB was populated with example data.");
        }

        entityManager.getTransaction().commit();
    }

    private static boolean hasData(EntityManager entityManager) {
        return (((Long) entityManager.createQuery("select count(a) from Address a").getSingleResult()) +
                ((Long) entityManager.createQuery("select count(p) from Person p").getSingleResult())) > 0;
    }
}
