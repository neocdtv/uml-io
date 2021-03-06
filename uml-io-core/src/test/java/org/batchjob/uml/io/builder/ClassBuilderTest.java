/*
 * Copyright 2017 Michael Schieder <michael.schieder(at)gmail.com>
 *
 * This file is part of uml-io.
 *
 * uml-io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * uml-io is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with uml-io.  If not, see <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 *
 */
package org.batchjob.uml.io.builder;

import static org.batchjob.uml.io.utils.ClassModelUtils.class_;
import static org.batchjob.uml.io.utils.ClassModelUtils.enumeration;
import static org.batchjob.uml.io.utils.ClassModelUtils.interface_;
import static org.batchjob.uml.io.utils.ClassModelUtils.model;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.batchjob.uml.io.utils.Uml2Utils;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.VisibilityKind;
import org.junit.Test;

public class ClassBuilderTest {
	@Test
	public void testFinal() {
		Model model = model("classModel").add(class_("MyClass").setFinal(true)).build();
		Class myClass = Uml2Utils.findClass("classModel::MyClass", model).get();
		assertThat(myClass, is(notNullValue()));
		assertThat(myClass.isFinalSpecialization(), is(true));
	}

	@Test
	public void testAbstract() {
		Model model = model("classModel").add(class_("MyClass").setAbstract(true)).build();
		Class myClass = Uml2Utils.findClass("classModel::MyClass", model).get();
		assertThat(myClass, is(notNullValue()));
		assertThat(myClass.isAbstract(), is(true));
	}

	@Test
	public void testVisibility() {
		Model model = model("classModel").add(class_("MyClass").setVisibility(VisibilityKind.PACKAGE_LITERAL)).build();
		assertThat(model, is(notNullValue()));
		Class myClass = Uml2Utils.findClass("classModel::MyClass", model).get();
		assertThat(myClass, is(notNullValue()));
		assertThat(myClass.getVisibility(), is(VisibilityKind.PACKAGE_LITERAL));
	}

	@Test
	public void testNestedClassifiers() {
		Model model = model("classModel")
				.add(class_("MyClass").add(class_("MyNestedClass")).add(enumeration("MyNestedEnum", "one", "two")))
				.build();
		assertThat(model, is(notNullValue()));
		Class myClass = Uml2Utils.findClass("classModel::MyClass", model).get();
		assertThat(myClass, is(notNullValue()));
		assertThat(myClass.getNestedClassifiers(), hasSize(2));
		assertThat(Uml2Utils.findElement("classModel::MyClass::MyNestedClass", model), is(notNullValue()));
		assertThat(Uml2Utils.findElement("classModel::MyClass::MyNestedEnum", model), is(notNullValue()));
		assertThat(Uml2Utils.findElement("classModel::MyClass::MyNestedEnum::one", model), is(notNullValue()));
	}

	@Test
	public void testInterfaceRealization() {
		Model model = model("classModel").add(class_("Customer").addRealization("classModel::IPerson"))
				.add(interface_("IPerson")).build();

		Class clazz = Uml2Utils.findElement("classModel::Customer", model);
		Interface ifc = Uml2Utils.findElement("classModel::IPerson", model);
		assertThat(ifc, is(notNullValue()));
		assertThat(clazz.getInterfaceRealizations(), hasSize(1));

		assertThat(clazz.getInterfaceRealizations().get(0).getImplementingClassifier().getQualifiedName(),
				is("classModel::Customer"));
		assertThat(clazz.getInterfaceRealizations().get(0).getSuppliers().get(0).getQualifiedName(),
				is("classModel::IPerson"));

	}
}
