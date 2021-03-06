/* Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.test

import org.springframework.validation.Errors

class MockUtilsSaveDomainTests extends GroovyTestCase {

    private @Lazy MetaTestHelper metaTestHelper = {
        MetaTestHelper result = new MetaTestHelper()
        result.classesUnderTest = [TestDomain, TestDomainWithClosureEventHandlers]
        result.classesToReset = [Errors]
        return result
    }()

    private Map errorsMap

    void setUp() {
        metaTestHelper.setUp()
        errorsMap = new IdentityHashMap()
    }

    void tearDown() {
        metaTestHelper.tearDown()
        MockUtils.resetIds()
    }

    /**
     * Tests the dynamically added <code>save()</code> method.
     */
    void testSave() {
        MockUtils.mockDomain(TestDomain, errorsMap)

        def domain = new TestDomain(name: "Alice Doe", country: "US", age: 35, title: "Ms.")
        assertEquals domain, domain.save()
        assertEquals 1, domain.id
        assertNotNull domain.dateCreated
        def updated = domain.lastUpdated
        assertNotNull updated

        /* Save again and lastUpdated should be set. */
        domain.save()
        assertTrue domain.lastUpdated > updated

    }

    /**
     * Tests the dynamically added <code>save()</code> method.
     */
    void testSaveWithArgs() {
        MockUtils.mockDomain(TestDomain, errorsMap)

        def domain = new TestDomain(name: "Alice Doe", country: "US", age: 35, title: "Ms.")
        assertEquals domain, domain.save(flush: true)
    }

    /**
     * Tests that the <code>save()</code> method does not add an existing
     * test instance to the original list of test instances. In other
     * words, if one of the original test instances is saved, the list
     * should <i>not</i> change in size.
     */
    void testSaveWithExistingInstance() {
        def testInstances = [ new TestDomain(name: "Alice Doe", country: "US", age: 35) ]
        MockUtils.mockDomain(TestDomain, errorsMap, testInstances)

        testInstances[0].save()
        assertEquals 1, testInstances.size()
    }

	/**
	 * Tests that the mocked <code>save()</code> method respects the <code>failOnError: true</code> argument.
	 */
	void testSaveWithFailOnErrorTrue() {
		MockUtils.mockDomain(TestDomain, errorsMap)

		def domain = new TestDomain()
		shouldFail(grails.validation.ValidationException) {
			domain.save(failOnError: true)
		}
	}

    void testInsertAndUpdateDomainEvents() {
        MockUtils.mockDomain(TestDomain, errorsMap)

        def domain = new TestDomain(name: "Alice Doe", country: "US", age: 35, title: "Ms.")

        assertEquals 0, domain.beforeInserted
        assertEquals 0, domain.afterInserted
        assertEquals 0, domain.beforeUpdated
        assertEquals 0, domain.afterUpdated

        assertNotNull domain.save()
        assertEquals 'beforeInsert was not called', 1, domain.beforeInserted
        assertEquals 'afterInsert was not called', 1, domain.afterInserted
        assertEquals 'beforeUpdated was unexpectedly called', 0, domain.beforeUpdated
        assertEquals 'afterUpdated was unexpectedly called', 0, domain.afterUpdated

        domain.save()
        assertEquals 'beforeInsert was called on subsequent save', 1, domain.beforeInserted
        assertEquals 'afterInsert was called on subsequent save', 1, domain.afterInserted
        assertEquals 'beforeUpdated was not called', 1, domain.beforeUpdated
        assertEquals 'afterUpdated was not called', 1, domain.afterUpdated
    }

    void testInsertAndUpdateEventsWithClosureHandlers() {
        MockUtils.mockDomain(TestDomainWithClosureEventHandlers, errorsMap)

        def domain = new TestDomainWithClosureEventHandlers(name: "Alice Doe")

        assertEquals 0, domain.beforeInserted
        assertEquals 0, domain.afterInserted
        assertEquals 0, domain.beforeUpdated
        assertEquals 0, domain.afterUpdated

        assertNotNull domain.save()
        assertEquals 'beforeInsert was not called', 1, domain.beforeInserted
        assertEquals 'afterInsert was not called', 1, domain.afterInserted
        assertEquals 'beforeUpdated was unexpectedly called', 0, domain.beforeUpdated
        assertEquals 'afterUpdated was unexpectedly called', 0, domain.afterUpdated

        domain.save()
        assertEquals 'beforeInsert was called on subsequent save', 1, domain.beforeInserted
        assertEquals 'afterInsert was called on subsequent save', 1, domain.afterInserted
        assertEquals 'beforeUpdated was not called', 1, domain.beforeUpdated
        assertEquals 'afterUpdated was not called', 1, domain.afterUpdated
    }

}
