using AutoMapper;
using DiscussionService.Dtos;
using DiscussionService.Models;

namespace DiscussionService.Profiles
{
    public class StudentProfile : Profile
    {
        public StudentProfile()
        {
            CreateMap<CreateStudentDto, Student>();
            CreateMap<Student, GetStudentDto>();
        }
    }
}